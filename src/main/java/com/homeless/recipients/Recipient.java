package com.homeless.recipients;

import java.util.List;
import java.util.Map;

public class Recipient {

  private int id;
  private String email;
  private Map<String, List<String>> filterMap;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Map<String, List<String>> getFilterMap() {
    return filterMap;
  }

  public void setFilterMap(Map<String, List<String>> filterMap) {
    this.filterMap = filterMap;
  }
}
