package com.homeless.actys;

import com.homeless.models.Rental;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ActysDetailsPageCrawler {
  public Rental getRentalDetails(String html) {
    Document document = Jsoup.parse(html);
    Rental ren = new Rental();
    ren.setUrl(document.select("link[rel=canonical]").attr("href"));
    ren.setPrice(getPrice(document));
    Elements elements =
        document.select(".table-unbordered.hidden-xs").select("tr").get(1).select("td");
    ren.setRoomCount(Integer.parseInt(elements.get(2).text().replace("kamers", "").trim()));
    ren.setArea(Integer.parseInt(elements.get(3).text().replace("m2", "").trim()));
    if (elements.get(4).text().contains("per direct")) {
      ren.setAvaiableDate(Instant.now().truncatedTo(ChronoUnit.DAYS));
    } else {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      ren.setAvaiableDate(Instant.from(dateTimeFormatter.parse(elements.get(4).text())));
    }
    ren.setAddress(
        document.select("h1[itemprop=streetAddress]").text()
            + " "
            + document.select("p[itemprop=addressLocality]").text());
    return ren;
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
