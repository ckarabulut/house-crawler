package com.homeless.actys;

import com.homeless.models.Rental;
import com.homeless.rentals.RentalsDao;
import com.homeless.models.Status;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ActysNewRentalFinder extends TimerTask {

  private final RentalsDao rentalsDao;
  private final ActysCrawler actysCrawler;

  public ActysNewRentalFinder(RentalsDao rentalsDao) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = new ActysCrawler();
  }

  public ActysNewRentalFinder(RentalsDao rentalsDao, ActysCrawler actysCrawler) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = actysCrawler;
  }

  @Override
  public void run() {

    List<Rental> crawledRentals = actysCrawler.getAllRentals();
    Map<String, Rental> crawledRentalNameMap =
        crawledRentals.stream().collect(Collectors.toMap(Rental::getAddress, rental -> rental));

    List<Rental> savedRentals = rentalsDao.findByStatus(Status.ACTIVE);
    Map<String, Rental> savedRentalNameMap =
        savedRentals.stream().collect(Collectors.toMap(Rental::getAddress, rental -> rental));

    crawledRentalNameMap
        .keySet()
        .stream()
        .filter(address -> !savedRentalNameMap.keySet().contains(address))
        .map(crawledRentalNameMap::get)
        .forEach(rentalsDao::insertRental);

    savedRentalNameMap
        .keySet()
        .stream()
        .filter(address -> !crawledRentalNameMap.keySet().contains(address))
        .map(savedRentalNameMap::get)
        .peek(rental -> rental.setStatus(Status.DELETED))
        .forEach(rentalsDao::updateRental);
  }
}
