package com.homeless;

import com.homeless.actys.ActysNewRentalFinder;
import com.homeless.config.Configuration;
import com.homeless.notification.EmailNotifier;

import java.util.Arrays;
import java.util.Timer;

public class Application {

  private DaoFactory daoFactory;
  private Configuration configuration;
  private EmailNotifier emailNotifier;

  public static void main(String[] args) {
    Application application = new Application();
    application.init();
    application.scheduleActys();
  }

  public void init() {
    configuration = Configuration.fromPropertiesFiles();
    daoFactory = new DaoFactory(configuration);
    emailNotifier = new EmailNotifier(configuration);
  }

  public void scheduleActys() {
    Timer t = new Timer();
    ActysNewRentalFinder crawler =
        new ActysNewRentalFinder(daoFactory.getRentalsDao(), emailNotifier);
    t.scheduleAtFixedRate(crawler, 0, 2 * 60 * 1000);
  }
}
