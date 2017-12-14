package com.homeless;

import com.homeless.actys.ActysNewRentalFinder;
import com.homeless.config.Configuration;

import java.util.Timer;

public class Application {

  private DaoFactory daoFactory;
  private Configuration configuration;

  public static void main(String[] args) {
    Application application = new Application();
    application.init();
    application.scheduleActys();
  }

  public void init() {
    configuration = Configuration.fromPropertiesFiles();
    daoFactory = new DaoFactory(configuration);
  }

  public void scheduleActys() {
    Timer t = new Timer();
    ActysNewRentalFinder crawler = new ActysNewRentalFinder(daoFactory.getRentalsDao());
    t.scheduleAtFixedRate(crawler, 0, 30 * 1000);
  }
}
