package com.homeless.proxies;

import java.util.Objects;

public class Proxy {
  private final String ip;
  private final int port;
  private final String code;
  private final String country;
  private final String proxyType;
  private final boolean google;
  private final boolean https;
  private final String lastChecked;
  private int reliability;

  public Proxy(
      String ip,
      int port,
      String code,
      String country,
      String proxyType,
      boolean google,
      boolean https,
      String lastChecked) {
    this.ip = ip;
    this.port = port;
    this.code = code;
    this.country = country;
    this.proxyType = proxyType;
    this.google = google;
    this.https = https;
    this.lastChecked = lastChecked;
    this.reliability = 10;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public String getCode() {
    return code;
  }

  public String getCountry() {
    return country;
  }

  public String getProxyType() {
    return proxyType;
  }

  public boolean isGoogle() {
    return google;
  }

  public boolean isHttps() {
    return https;
  }

  public String getLastChecked() {
    return lastChecked;
  }

  public boolean isReliable() {
    return reliability > 0;
  }

  public void reduceReliability() {
    reliability--;
  }

  @Override
  public String toString() {
    return "Proxy{"
        + "ip='"
        + ip
        + '\''
        + ", port="
        + port
        + ", code='"
        + code
        + '\''
        + ", country='"
        + country
        + '\''
        + ", proxyType='"
        + proxyType
        + '\''
        + ", google="
        + google
        + ", https="
        + https
        + ", lastChecked='"
        + lastChecked
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Proxy proxy = (Proxy) o;
    return port == proxy.port
        && google == proxy.google
        && https == proxy.https
        && Objects.equals(ip, proxy.ip)
        && Objects.equals(code, proxy.code)
        && Objects.equals(country, proxy.country)
        && Objects.equals(proxyType, proxy.proxyType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ip, port, code, country, proxyType, google, https);
  }
}
