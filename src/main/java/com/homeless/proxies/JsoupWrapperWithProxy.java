package com.homeless.proxies;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupWrapperWithProxy {

  private static ProxyPool proxyPool = new ProxyPool();

  public static Document getDocument(URI uri) {
    String url = uri.toString();
    return getDocument(url, false);
  }

  public static Document getDocument(String url, boolean highPriority) {
    for (int i = 0; i < 20; i++) {
      Proxy proxy = proxyPool.getNextProxy(highPriority);
      long start = System.currentTimeMillis();
      try {
        return Jsoup.connect(url)
            .proxy(proxy.getIp(), proxy.getPort())
            .header(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, br")
            .header("Accept-Language", "en-US,en;q=0.8,tr;q=0.6")
            .header("Cache-Control", "max-age=0")
            .header("Connection", "keep-alive")
            .header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
            .cookies(new HashMap<>())
            .timeout(8000)
            .get();
      } catch (IOException e) {
        proxyPool.reduceReliability(proxy);
      } finally {
        if (System.currentTimeMillis() - start < 2000L) {
          proxyPool.increaseReliability(proxy);
        }
      }
    }

    throw new RuntimeException("Max attempt number is exceed");
  }
}
