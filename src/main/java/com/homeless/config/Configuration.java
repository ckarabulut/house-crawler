package com.homeless.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Properties;

public class Configuration {

  private String dbUser;
  private String dbPassword;
  private String dbUrl;

  private Configuration() {}

  public static Configuration fromPropertiesFiles() {
    Properties properties = new Properties();
    try {
      properties.load(Configuration.class.getResourceAsStream("/application.properties"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Gson gson = new Gson();
    JsonElement jsonElement = gson.toJsonTree(properties);
    return gson.fromJson(jsonElement, Configuration.class);
  }

  public String getDbUser() {
    return dbUser;
  }

  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public void setDbUrl(String dbUrl) {
    this.dbUrl = dbUrl;
  }
}
