package com.homeless.actys;

import com.homeless.notification.NotificationController;
import com.homeless.rentals.RentalsDao;
import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActysNewRentalFinder implements Runnable {
  private static Logger logger = LoggerFactory.getLogger(ActysNewRentalFinder.class.getName());

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
      logger.info("Crawling has started.");
      doRun();
      logger.info("Crawling has ended in {} seconds", (System.currentTimeMillis() - s) / 1000);
    } catch (RuntimeException e) {
      logger.error(
          "Crawling has ended with an error in {} seconds.",
          (System.currentTimeMillis() - s) / 1000,
          e);
    }
  }

  private void doRun() {
    List<Rental> crawledRentals = actysCrawler.getAllRentals();
    List<Rental> savedRentals = rentalsDao.findAll();
    Map<String, Rental> savedRentalNameMap =
        savedRentals
            .stream()
            .collect(Collectors.toMap(rental -> rental.getUrl().toLowerCase(), rental -> rental));

    List<Rental> newRentals =
        crawledRentals
            .stream()
            .filter(Objects::nonNull)
            .filter(
                newRental -> {
                  String url = newRental.getUrl().toLowerCase();
                  Rental rental = savedRentalNameMap.get(url);
                  if (rental == null) {
                    rentalsDao.insertRental(newRental);
                    return newRental.getStatus() == Status.AVAILABLE;
                  }
                  savedRentalNameMap.remove(url);
                  newRental.setId(rental.getId());
                  rentalsDao.updateRental(newRental);
                  if (rental.getStatus() == newRental.getStatus()) {
                    return false;
                  }
                  return newRental.getStatus() == Status.AVAILABLE;
                })
            .collect(Collectors.toList());

    savedRentalNameMap
        .values()
        .stream()
        .filter(rental -> rental.getStatus() != Status.DELETED)
        .peek(rental -> rental.setStatus(Status.DELETED))
        .forEach(rentalsDao::updateRental);

    notificationController.sendRentalEmail(newRentals);
  }
}
