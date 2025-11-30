package org.fd.mcb.shared.notification.channel.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.notification.channel.NotificationChannel;
import org.fd.mcb.shared.notification.dto.NotificationData;
import org.fd.mcb.shared.notification.exception.NotificationException;
import org.fd.mcb.shared.notification.template.NotificationTemplateResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email implementation of NotificationChannel using Spring Mail.
 * Sends HTML emails using configured SMTP server.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "notification.channels.email",
        name = "enabled",
        havingValue = "true"
)
public class EmailNotificationChannel implements NotificationChannel {

    private final JavaMailSender mailSender;
    private final NotificationTemplateResolver templateResolver;

    @Override
    public void send(NotificationData notificationData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(notificationData.getRecipient().getEmail());
            helper.setSubject(notificationData.getSubject());

            // Resolve template and populate with variables
            String htmlContent = templateResolver.resolveEmailTemplate(
                    notificationData.getEvent(),
                    notificationData.getVariables()
            );
            helper.setText(htmlContent, true);  // true = HTML content

            mailSender.send(message);

            log.info("Email notification sent successfully to: {}",
                    notificationData.getRecipient().getEmail());

        } catch (MessagingException e) {
            log.error("Failed to create email message: {}", e.getMessage());
            throw new NotificationException("Failed to create email message", e);
        } catch (MailException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new NotificationException("Failed to send email", e);
        }
    }

    @Override
    public org.fd.mcb.shared.notification.enums.NotificationChannel getChannelType() {
        return org.fd.mcb.shared.notification.enums.NotificationChannel.EMAIL;
    }

    @Override
    public boolean isEnabled() {
        // If this component is loaded, the channel is enabled
        return true;
    }

    @Override
    public boolean canSend(NotificationData notificationData) {
        return notificationData != null
                && notificationData.getRecipient() != null
                && notificationData.getRecipient().hasEmail();
    }
}
