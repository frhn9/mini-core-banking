package org.fd.mcb.shared.notification.channel.impl;

import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.notification.channel.NotificationChannel;
import org.fd.mcb.shared.notification.dto.NotificationData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Logging implementation of NotificationChannel.
 * Logs notification details instead of actually sending them.
 * Useful for development, testing, and debugging.
 */
@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "notification.channels.log",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true  // Enabled by default
)
public class LoggingNotificationChannel implements NotificationChannel {

    @Override
    public void send(NotificationData notificationData) {
        log.info("============================================================");
        log.info("NOTIFICATION LOG");
        log.info("============================================================");
        log.info("Event Type: {}", notificationData.getEvent());
        log.info("Subject: {}", notificationData.getSubject());
        log.info("Recipient: {}", notificationData.getRecipient().getName());
        log.info("Email: {}", notificationData.getRecipient().getEmail());
        log.info("Phone: {}", notificationData.getRecipient().getPhoneNumber());
        log.info("------------------------------------------------------------");
        log.info("Variables:");
        notificationData.getVariables().forEach((key, value) ->
                log.info("  {} = {}", key, value)
        );
        log.info("============================================================");
    }

    @Override
    public org.fd.mcb.shared.notification.enums.NotificationChannel getChannelType() {
        return org.fd.mcb.shared.notification.enums.NotificationChannel.LOG;
    }

    @Override
    public boolean isEnabled() {
        return true;  // Always enabled when the component is loaded
    }

    @Override
    public boolean canSend(NotificationData notificationData) {
        // Logging channel can always send
        return notificationData != null && notificationData.getRecipient() != null;
    }
}
