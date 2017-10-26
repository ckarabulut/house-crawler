package com.homeless.actys;

import com.homeless.TestDataHelper;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.List;

public class ActysListPageCrawlerTest {

  @Test
  public void should_get_details_links() {
    ActysListPageCrawler actysListPageCrawler = new ActysListPageCrawler();

    String html = TestDataHelper.getResourceAsString("/ListPage.html");
    List<URI> pageDetailUrls = actysListPageCrawler.getCurrentPageDetailUrls(html);

    Assert.assertEquals(
        "/huurwoningen/veenendaal/veenendaal-willem-barentszstraat-125-1",
        pageDetailUrls.get(0).toString());

    Assert.assertEquals(
        "/huurwoningen/rhenen/rhenen-de-hollentoren-4", pageDetailUrls.get(1).toString());
  }

  @Test
  public void name() throws Exception {}

  @Test
  public void should_gather_all_urls() {}
}
