package com.homeless.filters;

import com.homeless.recipients.Recipient;
import com.homeless.rentals.models.Rental;

import java.util.List;

public class SizeFilter implements Filter {

    private static final String KEY = "area";

    @Override
    public boolean isValid(Recipient recipient, Rental rental) {
        if (!recipient.getFilterMap().containsKey(KEY)) {
            return true;
        }
        List<String> ranges = recipient.getFilterMap().get(KEY);
        int min = Integer.parseInt(ranges.get(0));
        int max = Integer.parseInt(ranges.get(1));
        return rental.getArea() >= min && rental.getArea() <= max;
    }
}
