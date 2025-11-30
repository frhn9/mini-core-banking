package org.fd.mcb.shared.notification.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the recipient of a notification.
 * Contains contact information for all supported notification channels.
 */
@Data
@Builder
public class NotificationRecipient {
    /**
     * Recipient's unique identifier (typically customer ID)
     */
    private Long recipientId;

    /**
     * Recipient's full name
     */
    private String name;

    /**
     * Email address for email notifications
     */
    private String email;

    /**
     * Phone number for SMS notifications (E.164 format recommended)
     */
    private String phoneNumber;

    /**
     * Device token for push notifications (FCM token)
     */
    private String deviceToken;

    /**
     * Preferred language/locale for notification templates
     */
    private String locale;

    /**
     * Checks if the recipient has a valid email address
     */
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    /**
     * Checks if the recipient has a valid phone number
     */
    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    /**
     * Checks if the recipient has a valid device token for push notifications
     */
    public boolean hasDeviceToken() {
        return deviceToken != null && !deviceToken.trim().isEmpty();
    }
}
