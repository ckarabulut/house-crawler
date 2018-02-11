package com.homeless.filters;

import com.homeless.recipients.Recipient;
import com.homeless.rentals.models.Rental;

import java.util.List;

public class RangeFilter implements Filter {

  private static final String RANGE_KEY = "range";

  @Override
  public boolean isValid(Recipient recipient, Rental rental) {
    if (!recipient.getFilterMap().containsKey(RANGE_KEY)) {
      return true;
    }
    List<String> ranges = recipient.getFilterMap().get(RANGE_KEY);
    int min = Integer.parseInt(ranges.get(0));
    int max = Integer.parseInt(ranges.get(1));
    return rental.getPrice() >= min && rental.getPrice() <= max;
  }
}
