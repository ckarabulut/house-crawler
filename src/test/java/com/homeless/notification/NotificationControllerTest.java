package com.homeless.notification;

import com.homeless.recipients.Recipient;
import com.homeless.recipients.RecipientsDao;
import com.homeless.rentals.models.Rental;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class NotificationControllerTest {

  @Test
  public void testEmailFilter() {
    RecipientsDao recipientsDao = Mockito.mock(RecipientsDao.class);
    EmailNotifier emailNotifier = Mockito.mock(EmailNotifier.class);

    Recipient recipient = new Recipient();
    recipient.setEmail("caglar@homeless.com");
    recipient.setFilterMap(new HashMap<>());
    recipient.getFilterMap().put("url", Arrays.asList("amsterdam", "haarlem"));

    Mockito.when(recipientsDao.findAll()).thenReturn(Collections.singletonList(recipient));

    Rental haarlem =
        new Rental.Builder()
            .setUrl(
                "https://www.wonenmetactys.nl/huurwoningen/haarlem/haarlem-raoul-wallenbergstraat-25")
            .build();
    Rental amsterdam =
        new Rental.Builder()
            .setUrl("https://www.wonenmetactys.nl/huurwoningen/Amsterdam/Amsterdam-fregelaan-41")
            .build();
    Rental losser =
        new Rental.Builder()
            .setUrl("https://www.wonenmetactys.nl/huurwoningen/losser/losser-hofkamp-121")
            .build();
    NotificationController controller = new NotificationController(recipientsDao, emailNotifier);
    controller.sendRentalEmail(Arrays.asList(losser, haarlem, amsterdam));

    Mockito.verify(emailNotifier)
        .sendEmail(Mockito.eq(Arrays.asList(haarlem, amsterdam)), Mockito.eq("caglar@homeless.com"));
  }
}
