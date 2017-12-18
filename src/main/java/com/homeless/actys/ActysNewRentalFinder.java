package com.homeless.actys;

import com.homeless.notification.NotificationController;
import com.homeless.rentals.RentalsDao;
import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ActysNewRentalFinder extends TimerTask {
  private static Logger logger = Logger.getLogger(ActysNewRentalFinder.class.getName());

  private final RentalsDao rentalsDao;
  private final ActysCrawler actysCrawler;
  private final NotificationController notificationController;

  public ActysNewRentalFinder(
      RentalsDao rentalsDao,
      ActysCrawler actysCrawler,
      NotificationController notificationController) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = actysCrawler;
    this.notificationController = notificationController;
  }

  public ActysNewRentalFinder(
      RentalsDao rentalsDao, NotificationController notificationController) {
    this.rentalsDao = rentalsDao;
    this.actysCrawler = new ActysCrawler();
    this.notificationController = notificationController;
  }

  @Override
  public void run() {
    long s = System.currentTimeMillis();
    try {
      logger.log(Level.INFO, "Crawling has started.");
      doRun();
      logger.log(
          Level.INFO,
          String.format(
              "Crawling has ended in %d seconds", (System.currentTimeMillis() - s) / 1000));
    } catch (RuntimeException e) {
      logger.log(
          Level.SEVERE,
          String.format(
              "Crawling has ended with an error in %d seconds.",
              (System.currentTimeMillis() - s) / 1000),
          e);
    }
  }

  private void doRun() {
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

    notificationController.sendRentalEmail(newRentals);
  }
}
