package com.homeless.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Configuration {

  private String dbUser;
  private String dbPassword;
  private String dbUrl;
  private String senderEmail;
  private String senderPassword;

  private Configuration() {}

  public static Configuration fromMap(Map<String, String> map) {
    Gson gson = new Gson();
    return gson.fromJson(gson.toJson(map), Configuration.class);
  }

  public static Configuration fromPropertiesFiles() {
    Properties properties = new Properties();
    try {
      properties.load(Configuration.class.getResourceAsStream("/application.properties"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    for (String key : properties.stringPropertyNames()) {
      String property = properties.getProperty(key);
      if (property.matches("^\\$\\{[A-Z_]+}$")) {
        String substring = property.substring(2, property.length() - 1);
        String getenv = System.getenv(substring);
        properties.setProperty(key, getenv);
      }
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

  public String getSenderEmail() {
    return senderEmail;
  }

  public void setSenderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
  }

  public String getSenderPassword() {
    return senderPassword;
  }

  public void setSenderPassword(String senderPassword) {
    this.senderPassword = senderPassword;
  }
}
