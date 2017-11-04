package com.homeless.actys;

import com.homeless.models.Rental;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ActysCrawlerTest {

  @Test
  public void test() {
    ActysListPageCrawler actysListPageCrawler = Mockito.mock(ActysListPageCrawler.class);
    URI link1 = URI.create("http://www.test.com/home1");
    URI link2 = URI.create("http://www.test.com/home2");

    Mockito.when(actysListPageCrawler.getAllPageUrls()).thenReturn(Arrays.asList(link1, link2));

    Rental rental1 = new Rental();
    Rental rental2 = new Rental();
    ActysDetailsPageCrawler detailsPageCrawler = Mockito.mock(ActysDetailsPageCrawler.class);
    Mockito.when(detailsPageCrawler.getRentalDetails(link1)).thenReturn(rental1);
    Mockito.when(detailsPageCrawler.getRentalDetails(link2)).thenReturn(rental2);

    ActysCrawler actysCrawler = new ActysCrawler(actysListPageCrawler, detailsPageCrawler);
    List<Rental> rentals = actysCrawler.getAllRentals();

    Assert.assertEquals(2, rentals.size());
    Assert.assertEquals(rental1, rentals.get(0));
    Assert.assertEquals(rental2, rentals.get(1));
  }
}
