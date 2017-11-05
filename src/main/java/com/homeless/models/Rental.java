package com.homeless.models;

import java.time.Instant;

public class Rental {

  private int id;
  private Status status;
  private int price;
  private String type;
  private int area;
  private int roomCount;
  private Instant availableDate;
  private Instant insertionDate;
  private String address;
  private String url;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getArea() {
    return area;
  }

  public void setArea(int area) {
    this.area = area;
  }

  public int getRoomCount() {
    return roomCount;
  }

  public void setRoomCount(int roomCount) {
    this.roomCount = roomCount;
  }

  public Instant getAvailableDate() {
    return availableDate;
  }

  public void setAvailableDate(Instant availableDate) {
    this.availableDate = availableDate;
  }

  public Instant getInsertionDate() {
    return insertionDate;
  }

  public void setInsertionDate(Instant insertionDate) {
    this.insertionDate = insertionDate;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public static class Builder {
    private Rental rental;

    public Builder() {
      rental = new Rental();
    }

    public Builder setId(int id) {
      this.rental.id = id;
      return this;
    }

    public Builder setStatus(Status status) {
      this.rental.status = status;
      return this;
    }

    public Builder setPrice(int price) {
      this.rental.price = price;
      return this;
    }

    public Builder setType(String type) {
      this.rental.type = type;
      return this;
    }

    public Builder setArea(int area) {
      this.rental.area = area;
      return this;
    }

    public Builder setRoomCount(int roomCount) {
      this.rental.roomCount = roomCount;
      return this;
    }

    public Builder setAvailableDate(Instant availableDate) {
      this.rental.availableDate = availableDate;
      return this;
    }

    public Builder setInsertionDate(Instant insertionDate) {
      this.rental.insertionDate = insertionDate;
      return this;
    }

    public Builder setAddress(String address) {
      this.rental.address = address;
      return this;
    }

    public Builder setUrl(String url) {
      this.rental.url = url;
      return this;
    }

    public Rental build() {
      return rental;
    }
  }
}
