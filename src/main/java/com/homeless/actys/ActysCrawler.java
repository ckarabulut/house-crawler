package com.homeless.actys;

import com.homeless.proxies.JsoupWrapperWithProxy;
import com.homeless.rentals.models.Rental;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ActysCrawler {

  private final String url = "https://ikwilhuren.nu/huurwoningen/pagina/";
  private final DateTimeFormatter dateTimeFormatter;

  public ActysCrawler() {
    this.dateTimeFormatter =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM yyyy")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter()
            .withLocale(new Locale("nl", "NL"))
            .withZone(ZoneId.of("Europe/Amsterdam"));
  }

  public List<Rental> getAllRentals() {
    Set<Element> elementSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    Element lastElement = null;
    for (int i = 1; true; i++) {
      Document doc = JsoupWrapperWithProxy.getDocument(url + i);
      Elements elements = doc.select(".woning .row .info");
      if (elements.isEmpty()) {
        break;
      }
      if (lastElement != null
          && elements.get(elements.size() - 1).text().equals(lastElement.text())) {
        break;
      }
      lastElement = elements.get(elements.size() - 1);
      elementSet.addAll(elements);
    }

    return elementSet.stream().map(this::createRental).collect(Collectors.toList());
  }

  private Rental createRental(Element element) {
    Rental rental = new Rental();
    rental.setUrl(element.select("div.adres a").attr("href"));
    rental.setPrice(getPrice(element));
    rental.setType(getDetailText(element, "soortobject"));
    String roomCount = getDetailText(element, "slaapkamers");
    rental.setRoomCount(roomCount == null ? 0 : Integer.parseInt(roomCount));
    Instant now = Instant.now();
    String availableDateText = getDetailText(element, "beschikbaarper");
    if (availableDateText == null || availableDateText.contains("in overleg")) {
      rental.setAvailableDate(null);
    } else if (availableDateText.contains("per direct")) {
      rental.setAvailableDate(now.truncatedTo(ChronoUnit.DAYS));
    } else {
      rental.setAvailableDate(Instant.from(dateTimeFormatter.parse(availableDateText)));
    }
    rental.setInsertionDate(now);
    rental.setLastUpdatedDate(now);
    rental.setAddress(element.select("div.adres a span.straat").text());
    return rental;
  }

  private String getDetailText(Element element, String s) {
    Elements elements = element.select(String.format("div.details.row .%s span", s));
    if (elements.size() < 2) {
      return null;
    }
    return elements.get(1).text();
  }

  private int getPrice(Element element) {
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMAN);
    Number parse;
    String priceString = "";
    try {
      priceString = getDetailText(element, "huurprijs").replaceAll("[^0-9.,-]", "");

      parse = formatter.parse(priceString);
    } catch (ParseException e) {
      throw new RuntimeException(String.format("Number %s can not be parsed", priceString));
    }
    return parse.intValue();
  }
}
