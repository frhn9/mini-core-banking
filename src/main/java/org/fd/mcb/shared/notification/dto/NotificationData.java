package org.fd.mcb.shared.notification.dto;

import lombok.Builder;
import lombok.Data;
import org.fd.mcb.shared.notification.enums.NotificationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains all data required to send a notification.
 * This object is passed to notification channels for processing.
 */
@Data
@Builder
public class NotificationData {
    /**
     * The type of event that triggered this notification
     */
    private NotificationEvent event;

    /**
     * The recipient of this notification
     */
    private NotificationRecipient recipient;

    /**
     * Subject/title of the notification
     */
    private String subject;

    /**
     * Template variables to be used in the notification message.
     * These are interpolated into the template based on the event type.
     *
     * Common variables include:
     * - customerName: Recipient's name
     * - accountNumber: Account number involved in transaction
     * - amount: Transaction amount
     * - transactionId: Unique transaction identifier
     * - balance: Current account balance
     * - availableBalance: Available balance after holds
     * - timestamp: Transaction timestamp
     * - authCode: Authorization code (for transfers)
     * - expiresAt: Expiration time (for transfers)
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    /**
     * Additional metadata for logging and debugging
     */
    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();

    /**
     * Convenience method to add a variable
     */
    public void addVariable(String key, Object value) {
        this.variables.put(key, value);
    }

    /**
     * Convenience method to add metadata
     */
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
}
