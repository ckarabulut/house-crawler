package com.homeless.actys;

import com.homeless.models.Rental;
import com.homeless.models.Status;
import com.homeless.notification.EmailNotifier;
import com.homeless.rentals.RentalsDao;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ActysNewRentalFinder extends TimerTask {

  private final RentalsDao rentalsDao;
  private final ActysCrawler actysCrawler;
  private final EmailNotifier emailNotifier;

  public ActysNewRentalFinder(
      RentalsDao rentalsDao, ActysCrawler actysCrawler, EmailNotifier emailNotifier) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = actysCrawler;
    this.emailNotifier = emailNotifier;
  }

  public ActysNewRentalFinder(RentalsDao rentalsDao, EmailNotifier emailNotifier) {

    this.rentalsDao = rentalsDao;
    this.actysCrawler = new ActysCrawler();
    this.emailNotifier = emailNotifier;
  }

  @Override
  public void run() {
    List<Rental> crawledRentals = actysCrawler.getAllRentals();
    Map<String, Rental> crawledRentalNameMap =
        crawledRentals
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Rental::getUrl, t -> t));

    List<Rental> savedRentals = rentalsDao.findByStatus(Status.ACTIVE);
    Map<String, Rental> savedRentalNameMap =
        savedRentals.stream().collect(Collectors.toMap(Rental::getUrl, rental -> rental));

    List<Rental> newRentals =
        crawledRentalNameMap
            .keySet()
            .stream()
            .filter(url -> !savedRentalNameMap.keySet().contains(url))
            .map(crawledRentalNameMap::get)
            .peek(rental -> rental.setStatus(Status.ACTIVE))
            .collect(Collectors.toList());

    newRentals.forEach(rentalsDao::insertRental);

    savedRentalNameMap
        .keySet()
        .stream()
        .filter(url -> !crawledRentalNameMap.keySet().contains(url))
        .map(savedRentalNameMap::get)
        .peek(rental -> rental.setStatus(Status.DELETED))
        .forEach(rentalsDao::updateRental);

    emailNotifier.sendRentalEmail(newRentals);
  }
}
