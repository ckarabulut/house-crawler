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

    Rental rental21 = Rental.builder().url("urlA").status(Status.AVAILABLE).build();
    Rental rental22 = Rental.builder().url("urlB").status(Status.AVAILABLE).build();
    Rental rental23 = Rental.builder().url("urlC").status(Status.UNDER_OPTION).build();
    Rental rental24 = Rental.builder().url("urlD").status(Status.DELETED).build();
    Rental rental25 = Rental.builder().url("urlE").status(Status.DELETED).build();
    Mockito.when(crawler.getAllRentals())
        .thenReturn(Arrays.asList(rental21, rental22, rental23, rental24, rental25));

    Rental rental11 = Rental.builder().url("urlE").status(Status.AVAILABLE).build();
    Rental rental12 = Rental.builder().url("urlD").status(Status.UNDER_OPTION).build();
    Rental rental13 = Rental.builder().url("urlF").status(Status.DELETED).build();
    Rental rental14 = Rental.builder().url("urlG").status(Status.AVAILABLE).build();
    Rental rental15 = Rental.builder().url("urlA").status(Status.AVAILABLE).build();
    Mockito.when(dao.findAll())
        .thenReturn(Arrays.asList(rental11, rental12, rental13, rental14, rental15));

    actysNewRentalFinder.run();

    Mockito.verify(dao, Mockito.times(1)).insertRental(rental22);
    Mockito.verify(dao, Mockito.times(1)).insertRental(rental23);
    Mockito.verify(dao, Mockito.times(1)).updateRental(rental24);
    Mockito.verify(dao, Mockito.times(1)).updateRental(rental25);
    Mockito.verify(dao, Mockito.times(1)).updateRental(rental14);
    Mockito.verify(dao, Mockito.times(2)).insertRental(Matchers.any());
    Mockito.verify(dao, Mockito.times(4)).updateRental(Matchers.any());
    Mockito.verify(notificationController).sendRentalEmail(Collections.singletonList(rental22));
  }
}
