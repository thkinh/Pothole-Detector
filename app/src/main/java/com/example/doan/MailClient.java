package com.example.doan;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import android.util.Log;
import java.util.concurrent.Executors;

public class MailClient {
    private final String from;
    private final String password;
    private final String host;

    // Default constructor
    public MailClient() {
        this.from = "thkinh2008@gmail.com";
        this.password = "yuemkfwazqipomrg"; // Make sure this is a secure app-specific password
        this.host = "smtp.gmail.com";
    }

    // Parameterized constructor
    public MailClient(String from, String password, String host) {
        this.from = from;
        this.password = password;
        this.host = host;


    }

    public void sendEmail(String to) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Set up properties for the SMTP server
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.ssl.enable", "true");
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "465");

                // Create a new session with an authenticator
                Session session = Session.getInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

                String subject;
                String body;
                subject = "Android Pot Hole Detector ";
                body = "You just Sign in to our app !!!";

                // Create the email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText(body);

                // Send the email
                Transport.send(message);

            } catch (MessagingException e) {
                Log.e("MailClient", "Failed to send email: ", e);
            }
        });
    }

}
