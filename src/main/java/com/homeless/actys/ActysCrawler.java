package com.homeless.actys;

import com.homeless.models.Rental;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class ActysCrawler {

  private final ActysListPageCrawler actysListPageCrawler;
  private final ActysDetailsPageCrawler detailsPageCrawler;

  public ActysCrawler() {
    this.actysListPageCrawler = new ActysListPageCrawler();
    this.detailsPageCrawler = new ActysDetailsPageCrawler();
  }

  public ActysCrawler(
      ActysListPageCrawler actysListPageCrawler, ActysDetailsPageCrawler detailsPageCrawler) {
    this.actysListPageCrawler = actysListPageCrawler;
    this.detailsPageCrawler = detailsPageCrawler;
  }

  public List<Rental> getAllRentals() {
    List<URI> allPageUrls = actysListPageCrawler.getAllPageUrls();
    return allPageUrls
        .parallelStream()
        .map(detailsPageCrawler::getRentalDetails)
        .collect(Collectors.toList());
  }
}
