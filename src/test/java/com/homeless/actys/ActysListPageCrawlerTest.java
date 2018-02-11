package com.homeless.actys;

import com.homeless.TestDataHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ActysListPageCrawlerTest {

  @Test
  public void should_get_details_links() {
    ActysListPageCrawler actysListPageCrawler = new ActysListPageCrawler();

    String html = TestDataHelper.getResourceAsString("/ListPage.html");
    Set<String> pageDetailUrls = actysListPageCrawler.getCurrentPageDetailUrls(html);

    Assert.assertTrue(
        pageDetailUrls.contains(
            "https://www.wonenmetactys.nl/huurwoningen/veenendaal/veenendaal-willem-barentszstraat-125-1"));
    Assert.assertTrue(
        pageDetailUrls.contains(
            "https://www.wonenmetactys.nl/huurwoningen/rhenen/rhenen-de-hollentoren-4"));
  }

  @Test
  public void name() throws Exception {}

  @Test
  public void should_gather_all_urls() {}
}
