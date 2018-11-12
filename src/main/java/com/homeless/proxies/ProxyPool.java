package com.homeless.proxies;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyPool {

  private static Logger logger = LoggerFactory.getLogger(ProxyPool.class.getName());
  private final Random rand;
  private List<Proxy> proxies;
  private List<Proxy> highPerformingProxies;

  public ProxyPool() {
    this.rand = new Random();
    this.highPerformingProxies = Collections.synchronizedList(new ArrayList<>());
    this.proxies = loadProxies();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.submit(
        (Runnable)
            () -> {
              while (true) {
                try {
                  Thread.sleep(TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES));
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
                this.proxies = loadProxies();
                this.highPerformingProxies = Collections.synchronizedList(new ArrayList<>());
              }
            });
  }

  private List<Proxy> loadProxies() {
    Document document;
    try {
      document = Jsoup.parse(new URL("https://free-proxy-list.net/"), 60000);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Elements elements = document.select("#proxylisttable tbody tr");
    Set<Proxy> proxySet =
        elements
            .stream()
            .map(
                e -> {
                  Elements td = e.select("td");
                  return new Proxy(
                      td.get(0).text(),
                      Integer.parseInt(td.get(1).text()),
                      td.get(2).text(),
                      td.get(3).text(),
                      td.get(4).text(),
                      td.get(5).text().equals("yes"),
                      td.get(6).text().equals("yes"));
                })
            .filter(e -> e.isHttps() && e.getProxyType().equals("elite proxy"))
            .collect(Collectors.toSet());
    if (this.proxies != null) {
      proxySet.addAll(this.proxies);
    }
    return Collections.synchronizedList(new ArrayList<>(proxySet));
  }

  public Proxy getNextProxy(boolean highPriority) {
    if ((highPriority && !highPerformingProxies.isEmpty())
        || (highPerformingProxies.size() > 20)
        || (highPerformingProxies.size() > 5 && rand.nextBoolean())
        || (highPerformingProxies.size() > 0 && rand.nextBoolean() && rand.nextBoolean())) {
      return highPerformingProxies.get(rand.nextInt(highPerformingProxies.size()));
    }
    if (proxies.isEmpty()) {
      throw new RuntimeException("No proxy is found!!");
    }
    return proxies.get(rand.nextInt(proxies.size()));
  }

  public void increaseReliability(Proxy proxy) {
    highPerformingProxies.add(proxy);
    proxies.remove(proxy);
  }

  public void reduceReliability(Proxy proxy) {
    highPerformingProxies.remove(proxy);
    proxy.reduceReliability();
    if (!proxy.isReliable()) {
      logger.info("Proxy is not reliable anymore, removing! {}.", proxy);
      proxies.remove(proxy);
    }
  }
}
