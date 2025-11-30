package org.fd.mcb.shared.notification.enums;

/**
 * Defines the available channels for sending notifications.
 * Multiple channels can be used simultaneously for a single notification.
 */
public enum NotificationChannel {
    /**
     * Email notification channel using Spring Mail
     */
    EMAIL,

    /**
     * SMS notification channel using third-party SMS provider
     */
    SMS,

    /**
     * Push notification channel for mobile devices using FCM
     */
    PUSH,

    /**
     * Logging channel for development and testing purposes
     */
    LOG
}
