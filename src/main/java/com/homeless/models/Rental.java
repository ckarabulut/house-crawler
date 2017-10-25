package com.homeless.models;

import java.time.Instant;

public class Rental {

  private String id;
  private Status status;
  private int price;
  private String type;
  private int area;
  private int roomCount;
  private Instant avaiableDate;
  private Instant createdAt;
  private String address;
  private String url;

  public String getId() {
    return id;
  }

  public void setId(String id) {
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

  public Instant getAvaiableDate() {
    return avaiableDate;
  }

  public void setAvaiableDate(Instant avaiableDate) {
    this.avaiableDate = avaiableDate;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
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
}
