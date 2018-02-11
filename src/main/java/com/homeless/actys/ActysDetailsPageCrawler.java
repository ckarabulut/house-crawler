package com.homeless.actys;

import com.homeless.rentals.models.Rental;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActysDetailsPageCrawler {

  private static Logger logger = Logger.getLogger(ActysDetailsPageCrawler.class.getName());
  private final DateTimeFormatter dateTimeFormatter;

  public ActysDetailsPageCrawler() {
    this.dateTimeFormatter =
        new DateTimeFormatterBuilder()
            .appendPattern("dd-MM-yyyy")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter()
            .withZone(ZoneId.of("Europe/Amsterdam"));
    ;
  }

  public Rental getRentalDetails(String html) {
    Document document = Jsoup.parse(html);
    return getRental(document);
  }

  public Rental getRentalDetails(URI uri) {
    Document parsed;
    try {
      parsed = Jsoup.parse(uri.toURL(), 60000);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      return getRental(parsed);
    } catch (RuntimeException e) {
      logger.log(
          Level.SEVERE, String.format("There is an error while parsing %s", uri.toString()), e);
      return null;
    }
  }

  private Rental getRental(Document document) {
    boolean isHouseGroup = document.select(".nav-tabs li a").get(2).text().equals("Woningtypen");
    if (isHouseGroup) {
      return null;
    }
    Rental rental = new Rental();
    rental.setUrl(document.select("link[rel=canonical]").attr("href"));
    rental.setPrice(getPrice(document));
    Elements elements =
        document.select(".table-unbordered.hidden-xs").select("tr").get(1).select("td");
    rental.setType(elements.get(0).text().trim());
    rental.setRoomCount(
        Integer.parseInt(elements.get(2).text().replace("kamers", "").replace("kamer", "").trim()));
    rental.setArea(Integer.parseInt(elements.get(3).text().replace("m2", "").trim()));
    Instant now = Instant.now();
    if (!elements.get(1).text().contains("Nieuwbouw")) {
      if (elements.get(4).text().contains("per direct")) {
        rental.setAvailableDate(now.truncatedTo(ChronoUnit.DAYS));
      } else if (!elements.get(4).text().contains("Binnenkort beschikbaar")) {
        rental.setAvailableDate(dateTimeFormatter.parse(elements.get(4).text(), Instant::from));
      }
    }
    rental.setInsertionDate(now);
    rental.setLastUpdatedDate(now);
    rental.setAddress(
        document.select("h1[itemprop=streetAddress]").text()
            + " "
            + document.select("p[itemprop=addressLocality]").text());
    return rental;
  }

  private int getPrice(Document document) {
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMAN);
    Number parse;
    String priceString = "";
    try {
      priceString =
          document
              .select(".result_summary")
              .select("div[itemprop*='address'] b")
              .text()
              .replaceAll("[^0-9.,-]", "");

      parse = formatter.parse(priceString);
    } catch (ParseException e) {
      throw new RuntimeException(String.format("Number %s can not be parsed", priceString));
    }
    return parse.intValue();
  }
}
