package com.homeless.actys;

import com.homeless.TestDataHelper;
import com.homeless.rentals.models.Rental;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ActysDetailsPageCrawlerTest {

  @Test
  public void should_get_all_details() {
    ActysDetailsPageCrawler crawler = new ActysDetailsPageCrawler();
    String html = TestDataHelper.getResourceAsString("/DetailsPage.html");
    Rental rental = crawler.getRentalDetails(html);
    Assert.assertEquals(
        "https://www.wonenmetactys.nl/huurwoningen/hilversum/hilversum-minckelersstraat-1-c",
        rental.getUrl());
    //        Assert.assertEquals("type", rental.getType());
    Assert.assertEquals(172, rental.getArea());
    Assert.assertEquals(1400, rental.getPrice());
    Assert.assertEquals(5, rental.getRoomCount());
    Assert.assertEquals("Minckelersstraat 1 C 1223 LB Hilversum", rental.getAddress());
    Assert.assertEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), rental.getAvailableDate());
  }
}
