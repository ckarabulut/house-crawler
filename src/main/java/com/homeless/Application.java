package com.homeless;

import com.homeless.actys.ActysNewRentalFinder;
import com.homeless.config.Configuration;
import com.homeless.notification.EmailNotifier;
import com.homeless.notification.NotificationController;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
    t.scheduleAtFixedRate(
        new TimerTask() {
          @Override
          public void run() {
            if (LocalTime.now().isBefore(LocalTime.parse("07:00:00"))
                || LocalTime.now().isAfter(LocalTime.parse("20:30:00"))) {
              return;
            }
            crawler.run();
          }
        },
        0,
        TimeUnit.MILLISECONDS.convert(15, TimeUnit.MINUTES));
  }
}
