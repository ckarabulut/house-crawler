package com.homeless.actys;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    Set<String> result = new HashSet<>();
    boolean stop = false;
    for (int i = 1; !stop; i++) {
      Set<String> urls = getCurrentPageDetailUrls(getDocument(url + i));
      stop = urls.isEmpty();
      result.addAll(urls);
    }
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
          .get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
