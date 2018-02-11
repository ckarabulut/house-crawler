package com.homeless.actys;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ActysListPageCrawler {

  private final String actysLandingPageUrl = "https://www.wonenmetactys.nl";
  private final String url = actysLandingPageUrl + "/huurwoningen/?pagina=";

  public Set<String> getCurrentPageDetailUrls(String html) {
    Document doc = Jsoup.parse(html);
    return getCurrentPageDetailUrls(doc);
  }

  private Set<String> getCurrentPageDetailUrls(Document doc) {
    Set<String> result = new HashSet<>();
    Elements elements = doc.select(".result_row");
    for (Element element : elements) {
      String url = actysLandingPageUrl + element.attr("href");
      Set<String> currentPageDetailUrls = getCurrentPageDetailUrls(getDocument(url));
      if (currentPageDetailUrls.isEmpty()) {
        result.add(url);
      } else {
        result.addAll(currentPageDetailUrls);
      }
    }
    return result;
  }

  public List<URI> getAllPageUrls() {
    Set<String> result = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    Set<Element> elementSet = Collections.newSetFromMap(new ConcurrentHashMap<Element, Boolean>());
    for (int i = 1; true; i++) {
      Document doc = getDocument(url + i);
      Elements elements = doc.select(".result_row");
      if (elements.isEmpty()) {
        break;
      }
      elementSet.addAll(elements);
    }
    getUrls(result, elementSet);

    return result
        .stream()
        .map(
            s -> {
              try {
                return new URI(s);
              } catch (URISyntaxException e) {
                throw new RuntimeException(e);
              }
            })
        .collect(Collectors.toList());
  }

  private void getUrls(Set<String> result, Collection<Element> elementSet) {
    elementSet
        .parallelStream()
        .forEach(
            element -> {
              String url = actysLandingPageUrl + element.attr("href");
              Document doc = getDocument(url);
              Elements elements = doc.select(".result_row");
              if (elements.isEmpty()) {
                result.add(url);
              } else {
                getUrls(result, elements);
              }
            });
  }

  protected Document getDocument(String url) {
    try {
      return Jsoup.connect(url)
          .header(
              "Accept",
              "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
          .header("Accept-Encoding", "gzip, deflate, br")
          .header("Accept-Language", "en-US,en;q=0.8,tr;q=0.6")
          .header("Cache-Control", "max-age=0")
          .header("Connection", "keep-alive")
          .header("Host", "www.wonenmetactys.nl")
          .header(
              "User-Agent",
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
          .timeout(60000)
          .get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
