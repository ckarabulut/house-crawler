package com.homeless.rentals.models;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rental {

  private int id;
  private Status status;
  private int price;
  private String type;
  private int area;
  private int roomCount;
  private int floor;
  private Instant availableDate;
  private Instant createdOn;
  private Instant updatedOn;
  private String address;
  private String url;
}
