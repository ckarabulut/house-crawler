package com.homeless;

import com.homeless.actys.ActysNewRentalFinder;
import com.homeless.config.Configuration;
import com.homeless.notification.EmailNotifier;
import com.homeless.notification.NotificationController;

import java.util.Timer;

public class Application {

  private DaoFactory daoFactory;
  private Configuration configuration;
  private NotificationController notificationController;

  public static void main(String[] args) {
    Application application = new Application();
    application.init();
    application.scheduleActys();
  }

  public void init() {
    configuration = Configuration.fromPropertiesFiles();
    daoFactory = new DaoFactory(configuration);
    notificationController =
        new NotificationController(daoFactory.getRecipientsDao(), new EmailNotifier(configuration));
  }

  public void scheduleActys() {
    Timer t = new Timer();
    ActysNewRentalFinder crawler =
        new ActysNewRentalFinder(daoFactory.getRentalsDao(), notificationController);
    t.scheduleAtFixedRate(crawler, 0, 5 * 60 * 1000);
  }
}
