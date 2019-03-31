package com.homeless.actys;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.homeless.proxies.JsoupWrapperWithProxy;
import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActysCrawler {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
    List<Rental> rentals = Collections.synchronizedList(new LinkedList<>());
    ExecutorService executorService = Executors.newCachedThreadPool();
    Element lastElement = null;
    for (int i = 1; true; i++) {
      Document doc = JsoupWrapperWithProxy.getDocument(rentalListUrl + i, true);
      Elements elements = doc.select(".woning .row .info");
      if (elements.isEmpty()) {
        break;
      }
      if (lastElement != null
          && elements.get(elements.size() - 1).text().equals(lastElement.text())) {
        break;
      }
      lastElement = elements.get(elements.size() - 1);
        elements.forEach(
                t ->
                        executorService.submit(
                                () -> {
                                    rentals.add(createRental(t));
                                }));
    }
    executorService.shutdown();
    try {
      executorService.awaitTermination(30, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      logger.error("Couldn't fetch all records on time", e);
    }
    return rentals;
  }

  private Rental createRental(Element element) {
    Rental rental = Rental.builder().build();
    rental.setUrl(
        actysUrl.substring(0, actysUrl.length() - 1)
            + element.select("div.adres a").attr("href").toLowerCase());
    try {
      rental.setAddress(element.select("div.adres a span.straat").text());
      rental.setPrice(getPrice(element));
      rental.setType(getRowText(element, "soortobject"));
      rental.setRoomCount(getRoomCount(element));
      Instant now = Instant.now();
      String availableDateText = getRowText(element, "beschikbaarper");
      if (availableDateText.isEmpty() || availableDateText.contains("in overleg")) {
        rental.setAvailableDate(null);
      } else if (availableDateText.contains("per direct")) {
        rental.setAvailableDate(now.truncatedTo(ChronoUnit.DAYS));
      } else {
        rental.setAvailableDate(Instant.from(dateTimeFormatter.parse(availableDateText)));
      }
      fillOtherDetails(rental);
      return rental;
    } catch (Exception e) {
      logger.error("Error while scraping details {}", rental.getUrl(), e);
      return null;
    }
  }

  private int getRoomCount(Element element) {
    String roomCount = getRowText(element, "slaapkamers");
    return roomCount.isEmpty() ? 0 : Integer.parseInt(roomCount);
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
    String priceString;
    try {
      priceString = getRowText(element, "huurprijs").replaceAll("[^0-9.,-]", "");
      parse = formatter.parse(priceString);
    } catch (ParseException e) {
      return 0;
    }
    return parse.intValue();
  }

  private void fillOtherDetails(Rental rental) {
    Document document = JsoupWrapperWithProxy.getDocument(rental.getUrl(), false);
    rental.setStatus(getStatus(document, rental));
    rental.setArea(getArea(document));
    rental.setFloor(getFloor(document));
      rental.setServiceFee(getServiceFee(document));
      rental.setTotalCost(rental.getPrice() + rental.getServiceFee());
  }

  private int getArea(Document document) {
    Elements areaElement = document.select("tr#Main_Woonopp .Text");
    if (areaElement.isEmpty()) {
      return 0;
    }
    String areaText = areaElement.get(0).text();
    return Integer.parseInt(areaText.split(" ")[0]);
  }

  private int getFloor(Document document) {
    Elements areaElement = document.select("tr#Main_Woonlaag .Text");
    if (areaElement.isEmpty()) {
      return 0;
    }
    String areaText = areaElement.get(0).text();
    return Integer.parseInt(areaText);
  }

    private int getServiceFee(Document document) {
        Elements areaElement = document.select("tr#Main_Servicekosten .Text");
        if (areaElement.isEmpty()) {
            return 0;
        }
        String areaText = areaElement.get(0).text();
        return Integer.parseInt(areaText.split(" ")[1]);
    }

  private Status getStatus(Document document, Rental rental) {
    Elements select = document.select("div.container.gegevens .kenmerk span");
    Status status = Status.AVAILABLE;
    if (!select.isEmpty()) {
      String statusText = select.get(0).text();
      switch (statusText) {
        case "Nieuw!":
        case "Beschikbaar":
        case "Open huis":
          break;
        case "Onder optie":
          status = Status.UNDER_OPTION;
          break;
        case "Verhuurd":
          status = Status.DELETED;
          break;
        default:
          logger.warn("Unknown status {} for {}", statusText, rental.getUrl());
          break;
      }
    } else {
      logger.warn("No status found for {}", rental.getUrl());
    }
    return status;
  }
}
