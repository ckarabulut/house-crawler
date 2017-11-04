package com.homeless.actys;

import com.homeless.RentalsDao;
import com.homeless.models.Rental;

import java.util.List;
import java.util.TimerTask;

public class Crawler extends TimerTask {

  private final RentalsDao rentalsDao;
  private final ActysCrawler actysCrawler;

  public Crawler(RentalsDao rentalsDao) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = new ActysCrawler();
  }

  public Crawler(RentalsDao rentalsDao, ActysCrawler actysCrawler) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = actysCrawler;
  }

  @Override

  public void run() {

    List<Rental> allRentals = actysCrawler.getAllRentals();
//    allRentals.forEach(rental -> rentalsDao.findRental(rental.getId()));
    List<Rental> rentals = rentalsDao.getRentals();

  }
}
