package com.homeless.notification;

import com.homeless.filters.UrlFilter;
import com.homeless.recipients.RecipientsDao;
import com.homeless.rentals.models.Rental;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationController {

  private final UrlFilter urlFilter;
  private final EmailNotifier emailNotifier;
  private final RecipientsDao recipientsDao;

  public NotificationController(RecipientsDao recipientsDao, EmailNotifier emailNotifier) {
    this.recipientsDao = recipientsDao;
    this.urlFilter = new UrlFilter();
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
                        .filter(r -> urlFilter.isValidate(t, r))
                        .collect(Collectors.toList()),
                    t.getEmail()));
  }
}
