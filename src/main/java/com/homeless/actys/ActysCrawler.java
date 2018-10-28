package com.homeless.actys;

import com.homeless.proxies.JsoupWrapperWithProxy;
import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;
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

  private final String actysUrl = "https://ikwilhuren.nu/";
  private final String rentalListUrl = actysUrl + "huurwoningen/pagina/";
  private final DateTimeFormatter dateTimeFormatter;
  private final Locale dutchLocale;

  public ActysCrawler() {
    dutchLocale = new Locale("nl", "NL");
    this.dateTimeFormatter =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM yyyy")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter()
            .withLocale(dutchLocale)
            .withZone(ZoneId.of("Europe/Amsterdam"));
  }

  public List<Rental> getAllRentals() {
    Set<Element> elementSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    Element lastElement = null;
    for (int i = 1; true; i++) {
      Document doc = JsoupWrapperWithProxy.getDocument(rentalListUrl + i);
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

    return elementSet.parallelStream().map(this::createRental).collect(Collectors.toList());
  }

  private Rental createRental(Element element) {
    Rental rental = new Rental();
    rental.setUrl(element.select("div.adres a").attr("href"));
    rental.setPrice(getPrice(element));
    rental.setType(getRowText(element, "soortobject"));
    String roomCount = getRowText(element, "slaapkamers");
    rental.setRoomCount(roomCount.isEmpty() ? 0 : Integer.parseInt(roomCount));
    Instant now = Instant.now();
    String availableDateText = getRowText(element, "beschikbaarper");
    if (availableDateText.isEmpty() || availableDateText.contains("in overleg")) {
      rental.setAvailableDate(null);
    } else if (availableDateText.contains("per direct")) {
      rental.setAvailableDate(now.truncatedTo(ChronoUnit.DAYS));
    } else {
      rental.setAvailableDate(Instant.from(dateTimeFormatter.parse(availableDateText)));
    }
    rental.setInsertionDate(now);
    rental.setLastUpdatedDate(now);
    rental.setAddress(element.select("div.adres a span.straat").text());
    fillOtherDetails(rental);
    return rental;
  }

  private String getRowText(Element element, String s) {
    Elements elements = element.select(String.format("div.details.row .%s span", s));
    if (elements.size() < 2) {
      return "";
    }
    return elements.get(1).text();
  }

  private int getPrice(Element element) {
    NumberFormat formatter = NumberFormat.getNumberInstance(dutchLocale);
    Number parse;
    String priceString = "";
    try {
      priceString = getRowText(element, "huurprijs").replaceAll("[^0-9.,-]", "");

      parse = formatter.parse(priceString);
    } catch (ParseException e) {
      throw new RuntimeException(String.format("Number %s can not be parsed", priceString));
    }
    return parse.intValue();
  }

  private void fillOtherDetails(Rental rental) {
    Document document = JsoupWrapperWithProxy.getDocument(actysUrl + rental.getUrl());
    String status = document.select("div.container.gegevens .kenmerk span").get(0).text();
    if (status.equals("Nieuw!")) {
      rental.setStatus(Status.AVAILABLE);
    } else if (status.equals("Onder optie")) {
      rental.setStatus(Status.UNDER_OPTION);
    } else if (status.equals("Verhuurd")) {
      rental.setStatus(Status.DELETED);
    }

    String areaText = document.select("tr#Main_Woonopp .Text").get(0).text();
    int area = Integer.parseInt(areaText.split(" ")[0]);
    rental.setArea(area);
  }
}
