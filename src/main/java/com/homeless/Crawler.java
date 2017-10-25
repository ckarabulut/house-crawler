package com.homeless;

import java.util.TimerTask;

public class Crawler extends TimerTask {


  private RentalsDao rentalsDao;

  public Crawler(RentalsDao rentalsDao) {
    this.rentalsDao = rentalsDao;
  }

  @Override
  public void run() {
    rentalsDao.getRentals();
  }
}
