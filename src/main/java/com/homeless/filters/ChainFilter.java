package com.homeless.filters;

import com.homeless.recipients.Recipient;
import com.homeless.rentals.models.Rental;

import java.util.List;

public class ChainFilter implements Filter {

  private List<Filter> list;

  public ChainFilter(List<Filter> list) {

    this.list = list;
  }

  @Override
  public boolean isValid(Recipient recipient, Rental rental) {
    for (Filter filter : list) {
      if (!filter.isValid(recipient, rental)) {
        return false;
      }
    }
    return true;
  }
}
