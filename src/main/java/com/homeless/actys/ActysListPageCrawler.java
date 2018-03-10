package com.homeless.actys;

import com.homeless.proxies.JsoupWrapperWithProxy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
      Document document = JsoupWrapperWithProxy.getDocument(url);
      Set<String> currentPageDetailUrls = getCurrentPageDetailUrls(document);
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
      Document doc = JsoupWrapperWithProxy.getDocument(url + i);
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
              Document doc = JsoupWrapperWithProxy.getDocument(url);
              Elements elements = doc.select(".result_row");
              if (elements.isEmpty()) {
                result.add(url);
              } else {
                getUrls(result, elements);
              }
            });
  }
}
