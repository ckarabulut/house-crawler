package com.homeless.actys;

import com.homeless.models.Rental;
import com.homeless.rentals.RentalsDao;
import com.homeless.models.Status;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

public class ActysNewRentalFinderTest {

  @Test
  public void test() {
    RentalsDao dao = Mockito.mock(RentalsDao.class);
    ActysCrawler crawler = Mockito.mock(ActysCrawler.class);
    ActysNewRentalFinder actysNewRentalFinder = new ActysNewRentalFinder(dao, crawler);

    Rental rental2 = new Rental.Builder().setAddress("address2").build();
    Rental rental1 = new Rental.Builder().setAddress("address1").build();
    Rental rental3 = new Rental.Builder().setAddress("address3").build();
    Mockito.when(dao.findByStatus(Status.ACTIVE)).thenReturn(Arrays.asList(rental1, rental2));
    Mockito.when(crawler.getAllRentals()).thenReturn(Arrays.asList(rental2, rental3));

    actysNewRentalFinder.run();

    Mockito.verify(dao).insertRental(rental3);
    Mockito.verify(dao).updateRental(rental1);
  }
}
