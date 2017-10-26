package com.homeless.actys;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ActysListPageCrawler {

  private final String url = "https://www.wonenmetactys.nl/huurwoningen/?pagina=";

  public List<URI> getCurrentPageDetailUrls(String html) {
    Document doc = Jsoup.parse(html);
    List<URI> result = new ArrayList<>();
    Elements elements = doc.select(".result_row.spotlight");
    for (Element element : elements) {
      result.add(URI.create(element.attr("href")));
    }
    return result;
  }

  public List<URI> getAllPageUrls() {
    List<URI> result = new ArrayList<>();
    boolean stop = false;
    for (int i = 0; !stop; i++) {
      try {
        List<URI> urls = getCurrentPageDetailUrls(getDocument(i).text());
        stop = urls.isEmpty();
        result.addAll(urls);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  protected Document getDocument(int i) throws IOException {
    return Jsoup.connect(url + i).get();
  }
}
