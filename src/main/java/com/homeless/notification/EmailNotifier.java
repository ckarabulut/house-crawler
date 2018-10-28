package com.homeless.notification;

import com.homeless.config.Configuration;
import com.homeless.rentals.models.Rental;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotifier {

  private final String username;
  private final String password;

  public EmailNotifier(Configuration configuration) {
    if (configuration.getSenderEmail() == null || configuration.getSenderEmail().isEmpty()) {

      throw new RuntimeException("Empty sender");
    }
    if (configuration.getSenderPassword() == null || configuration.getSenderPassword().isEmpty()) {
      throw new RuntimeException("Empty sender password");
    }
    this.username = configuration.getSenderEmail();
    this.password = configuration.getSenderPassword();
  }

  public void sendEmail(List<Rental> rentalList, String email) {
    if (rentalList.isEmpty()) {
      return;
    }
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session =
        Session.getInstance(
            props,
            new Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
              }
            });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));

      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
      message.setSubject("Homeless rentals");
      message.setText(
          "Dear Homeless member,\n\n"
              + "We found these new rentals for you!\n\n"
              + rentalList.stream().map(Rental::getUrl).collect(Collectors.joining("\n"))
              + "\n\nHomeless team");

      Transport.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
