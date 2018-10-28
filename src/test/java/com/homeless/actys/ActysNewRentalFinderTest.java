package com.homeless.actys;

import com.homeless.notification.NotificationController;
import com.homeless.rentals.RentalsDao;
import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

public class ActysNewRentalFinderTest {

  @Test
  public void test() {
    RentalsDao dao = Mockito.mock(RentalsDao.class);
    ActysCrawler crawler = Mockito.mock(ActysCrawler.class);
    NotificationController notificationController = Mockito.mock(NotificationController.class);
    ActysNewRentalFinder actysNewRentalFinder =
        new ActysNewRentalFinder(dao, crawler, notificationController);

    Rental rental21 = new Rental.Builder().setUrl("urlA").setStatus(Status.AVAILABLE).build();
    Rental rental22 = new Rental.Builder().setUrl("urlB").setStatus(Status.AVAILABLE).build();
    Rental rental23 = new Rental.Builder().setUrl("urlC").setStatus(Status.UNDER_OPTION).build();
    Rental rental24 = new Rental.Builder().setUrl("urlD").setStatus(Status.DELETED).build();
    Rental rental25 = new Rental.Builder().setUrl("urlE").setStatus(Status.DELETED).build();
    Mockito.when(crawler.getAllRentals())
        .thenReturn(Arrays.asList(rental21, rental22, rental23, rental24, rental25));

    Rental rental11 = new Rental.Builder().setUrl("urlE").setStatus(Status.AVAILABLE).build();
    Rental rental12 = new Rental.Builder().setUrl("urlD").setStatus(Status.UNDER_OPTION).build();
    Rental rental13 = new Rental.Builder().setUrl("urlF").setStatus(Status.DELETED).build();
    Rental rental14 = new Rental.Builder().setUrl("urlG").setStatus(Status.AVAILABLE).build();
    Rental rental15 = new Rental.Builder().setUrl("urlA").setStatus(Status.AVAILABLE).build();
    Mockito.when(dao.findAll())
        .thenReturn(Arrays.asList(rental11, rental12, rental13, rental14, rental15));

    actysNewRentalFinder.run();

    Mockito.verify(dao, Mockito.times(1)).insertRental(rental22);
    Mockito.verify(dao, Mockito.times(1)).insertRental(rental23);
    Mockito.verify(dao, Mockito.times(1)).updateRental(rental24);
    Mockito.verify(dao, Mockito.times(1)).updateRental(rental25);
    Mockito.verify(dao, Mockito.times(1)).updateRental(rental14);
    Mockito.verify(dao, Mockito.times(2)).insertRental(Matchers.any());
    Mockito.verify(dao, Mockito.times(3)).updateRental(Matchers.any());
    Mockito.verify(notificationController).sendRentalEmail(Collections.singletonList(rental22));
  }
}
