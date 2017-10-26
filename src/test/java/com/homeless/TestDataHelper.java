package com.homeless;

import java.util.Scanner;

public class TestDataHelper {

  public static String getResourceAsString(String path) {
    Scanner s =
        new Scanner(TestDataHelper.class.getResourceAsStream(path)).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
