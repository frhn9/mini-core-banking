package org.fd.mcb.shared.notification.channel;

import org.fd.mcb.shared.notification.dto.NotificationData;

/**
 * Strategy interface for different notification channels.
 * Each implementation handles a specific notification delivery method
 * (email, SMS, push notification, etc.).
 */
public interface NotificationChannel {

    /**
     * Sends a notification through this channel.
     *
     * @param notificationData the notification data including recipient and message details
     * @throws NotificationException if the notification fails to send
     */
    void send(NotificationData notificationData);

    /**
     * Returns the channel type this implementation handles.
     *
     * @return the notification channel type
     */
    org.fd.mcb.shared.notification.enums.NotificationChannel getChannelType();

    /**
     * Checks if this channel is enabled and properly configured.
     *
     * @return true if the channel is ready to send notifications
     */
    boolean isEnabled();

    /**
     * Validates that the recipient has the necessary contact information
     * for this channel (e.g., email address for email channel).
     *
     * @param notificationData the notification data to validate
     * @return true if the recipient can receive notifications via this channel
     */
    boolean canSend(NotificationData notificationData);
}
