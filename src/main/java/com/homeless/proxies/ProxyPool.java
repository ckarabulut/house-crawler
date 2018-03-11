package com.homeless.proxies;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProxyPool {

  private static Logger logger = Logger.getLogger(ProxyPool.class.getName());
  private AtomicInteger counter;
  private List<Proxy> proxies;

  public ProxyPool() {
    this.proxies = loadProxies();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.submit(
        (Runnable)
            () -> {
              while (true) {
                try {
                  Thread.sleep(30 * 60 * 1000);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
                proxies = loadProxies();
              }
            });
  }

  private List<Proxy> loadProxies() {
    counter = new AtomicInteger();
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
            .distinct()
            .collect(Collectors.toSet());
    if (this.proxies != null) {
      proxySet.addAll(this.proxies);
    }
    return Collections.synchronizedList(new ArrayList<>(proxySet));
  }

  public Proxy getNextProxy() {
    if (proxies.isEmpty()) {
      throw new RuntimeException("No proxy is found!!");
    }
    Proxy proxy = proxies.get(counter.getAndIncrement() % proxies.size());
    if (!proxy.isReliable()) {
      logger.log(
          Level.WARNING, String.format("Proxy is not reliable anymore, removing!\n%s.", proxy));
      proxies.remove(proxy);
      return getNextProxy();
    }
    return proxy;
  }
}
