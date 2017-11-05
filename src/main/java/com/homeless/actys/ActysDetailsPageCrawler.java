package com.homeless.actys;

import com.homeless.models.Rental;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ActysDetailsPageCrawler {
  public Rental getRentalDetails(String html) {
    Document document = Jsoup.parse(html);
    return getRental(document);
  }

  public Rental getRentalDetails(URI uri) {
    Document parsed = null;
    try {
      parsed = Jsoup.parse(uri.toURL(), 60000);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return getRental(parsed);
  }

  private Rental getRental(Document document) {
    Rental rental = new Rental();
    rental.setUrl(document.select("link[rel=canonical]").attr("href"));
    rental.setPrice(getPrice(document));
    Elements elements =
        document.select(".table-unbordered.hidden-xs").select("tr").get(1).select("td");
    rental.setRoomCount(Integer.parseInt(elements.get(2).text().replace("kamers", "").trim()));
    rental.setArea(Integer.parseInt(elements.get(3).text().replace("m2", "").trim()));
    if (elements.get(4).text().contains("per direct")) {
      rental.setAvailableDate(Instant.now().truncatedTo(ChronoUnit.DAYS));
    } else {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      rental.setAvailableDate(Instant.from(dateTimeFormatter.parse(elements.get(4).text())));
    }
    rental.setAddress(
        document.select("h1[itemprop=streetAddress]").text()
            + " "
            + document.select("p[itemprop=addressLocality]").text());
    return rental;
  }

  private int getPrice(Document document) {
    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMAN);
    Number parse = null;
    try {
      parse =
          formatter.parse(
              document
                  .select("tr.light-green")
                  .select("td.table-cell--unbordered")
                  .select("span")
                  .text()
                  .replace("€", "")
                  .replace("p/mnd", "")
                  .trim());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return parse.intValue();
  }
}
