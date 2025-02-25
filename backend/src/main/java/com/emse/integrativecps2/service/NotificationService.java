package com.emse.integrativecps2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    private final Set<String> sentNotifications = new HashSet<>();

    /**
     * Sends an email notification only once per unique condition.
     * @param recipientEmail The email of the user
     * @param subject Email subject
     * @param message Email body content
     */
    public void sendEmail(String recipientEmail, String subject, String message) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            System.out.println("⚠️ No recipient email provided. Skipping email.");
            return;
        }

        String notificationKey = recipientEmail + "|" + subject;

        if (sentNotifications.contains(notificationKey)) {
            System.out.println("ℹ️ Notification already sent. Skipping duplicate email to: " + recipientEmail);
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
        sentNotifications.add(notificationKey);
        System.out.println("✅ Email sent to: " + recipientEmail);
    }
}
