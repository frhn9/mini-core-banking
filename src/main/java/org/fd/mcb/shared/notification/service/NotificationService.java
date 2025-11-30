package org.fd.mcb.shared.notification.service;

import org.fd.mcb.shared.notification.dto.NotificationData;
import org.fd.mcb.shared.notification.enums.NotificationChannel;

import java.util.Set;

/**
 * Service for sending notifications through multiple channels.
 * Coordinates the delivery of notifications via email, SMS, push, etc.
 */
public interface NotificationService {

    /**
     * Sends a notification through the specified channels.
     * Channels that fail to send will be logged but won't throw exceptions.
     *
     * @param notificationData the notification data
     * @param channels the channels to use for sending
     */
    void send(NotificationData notificationData, Set<NotificationChannel> channels);

    /**
     * Sends a notification through a single channel.
     *
     * @param notificationData the notification data
     * @param channel the channel to use
     */
    void send(NotificationData notificationData, NotificationChannel channel);

    /**
     * Sends a notification asynchronously through the specified channels.
     * The method returns immediately without waiting for the notification to be sent.
     *
     * @param notificationData the notification data
     * @param channels the channels to use for sending
     */
    void sendAsync(NotificationData notificationData, Set<NotificationChannel> channels);

    /**
     * Sends a notification asynchronously through a single channel.
     *
     * @param notificationData the notification data
     * @param channel the channel to use
     */
    void sendAsync(NotificationData notificationData, NotificationChannel channel);
}
