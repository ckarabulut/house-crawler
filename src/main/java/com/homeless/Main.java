package com.homeless;

import com.homeless.actys.Crawler;

import java.util.Timer;

public class Main {
  public static void main(String[] args) {
    Timer t = new Timer();
    Crawler crawler = new Crawler(null);
    t.scheduleAtFixedRate(crawler, 0, 10 * 1000);
  }
}
