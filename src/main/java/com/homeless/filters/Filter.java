package com.homeless.filters;

import com.homeless.recipients.Recipient;
import com.homeless.rentals.models.Rental;

public interface Filter {

  boolean isValid(Recipient recipient, Rental rental);
}
