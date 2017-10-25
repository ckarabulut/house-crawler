package com.homeless;

import java.util.Timer;

public class Main {
  public static void main(String[] args) {
    Timer t = new Timer();
    Crawler crawler = new Crawler();
    t.scheduleAtFixedRate(crawler, 0, 10 * 1000);
  }
}
