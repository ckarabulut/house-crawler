package com.homeless.filters;

import com.homeless.recipients.Recipient;
import com.homeless.rentals.models.Rental;

public class UrlFilter implements Filter {

  private static final String URL_KEY = "url";

  @Override
  public boolean isValid(Recipient recipient, Rental rental) {
    if (!recipient.getFilterMap().containsKey(URL_KEY)) {
      return true;
    }
    String url = rental.getUrl().toLowerCase();
    return recipient
        .getFilterMap()
        .get(URL_KEY)
        .stream()
        .anyMatch(t -> url.contains(t.toLowerCase()));
  }
}
