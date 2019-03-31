package com.homeless.notification;

import com.homeless.filters.*;
import com.homeless.recipients.RecipientsDao;
import com.homeless.rentals.models.Rental;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationController {

  private final Filter filter;
  private final EmailNotifier emailNotifier;
  private final RecipientsDao recipientsDao;

  public NotificationController(RecipientsDao recipientsDao, EmailNotifier emailNotifier) {
      this.filter = new ChainFilter(Arrays.asList(new UrlFilter(), new RangeFilter(), new SizeFilter()));
    this.recipientsDao = recipientsDao;
    this.emailNotifier = emailNotifier;
  }

  public void sendRentalEmail(List<Rental> rentalList) {
    recipientsDao
        .findAll()
        .forEach(
            t ->
                emailNotifier.sendEmail(
                    rentalList
                        .stream()
                        .filter(r -> filter.isValid(t, r))
                        .collect(Collectors.toList()),
                    t.getEmail()));
  }
}
