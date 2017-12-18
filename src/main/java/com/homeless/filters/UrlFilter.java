package com.homeless.filters;

import com.homeless.recipients.Recipient;
import com.homeless.rentals.models.Rental;

public class UrlFilter {

  private static final String URL_KEY = "url";

  public boolean isValidate(Recipient recipient, Rental rental) {
    if (!recipient.getFilterMap().containsKey(URL_KEY)) {
      return true;
    }
    String url = rental.getUrl().toLowerCase();
    return recipient
            .getFilterMap()
            .get(URL_KEY)
            .stream()
            .filter(t -> url.contains(t.toLowerCase()))
            .count()
        > 0;
  }
}
