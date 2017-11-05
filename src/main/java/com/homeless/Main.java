package com.homeless;

import com.homeless.actys.ActysNewRentalFinder;

import java.util.Timer;

public class Main {
  public static void main(String[] args) {
    Timer t = new Timer();
    ActysNewRentalFinder crawler = new ActysNewRentalFinder(null);
    t.scheduleAtFixedRate(crawler, 0, 10 * 1000);
  }
}
