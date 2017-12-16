package com.homeless.notification;

import com.homeless.config.Configuration;
import com.homeless.models.Rental;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class EmailNotifier {

  private final List<String> recipientList;
  private final String username;
  private final String password;

  public EmailNotifier(Configuration configuration) {
    recipientList = Arrays.asList(configuration.getRecipients().split(","));
    if (recipientList.isEmpty()) {
      throw new RuntimeException("Recipient list is empty");
    }
    if (configuration.getSenderEmail() == null || configuration.getSenderEmail().isEmpty()) {
      throw new RuntimeException("Empty sender");
    }
    if (configuration.getSenderPassword() == null || configuration.getSenderPassword().isEmpty()) {
      throw new RuntimeException("Empty sender password");
    }
    username = configuration.getSenderEmail();
    password = configuration.getSenderPassword();
  }

  public void sendRentalEmail(List<Rental> rentalList) {
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
            new javax.mail.Authenticator() {
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
              }
            });

    try {

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));

      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse(recipientList.stream().collect(Collectors.joining(","))));
      message.setSubject("Homeless rentals");
      message.setText(
          "Dear Homeless member,\n\n"
              + "We found these new rentals for ya!\n\n"
              + rentalList.stream().map(Rental::getUrl).collect(Collectors.joining("\n")));

      Transport.send(message);

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
