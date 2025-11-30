package org.fd.mcb.shared.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the types of banking events that trigger notifications.
 * Each event type is associated with a specific notification template.
 */
@Getter
@RequiredArgsConstructor
public enum NotificationEvent {
    /**
     * Successful deposit transaction
     */
    DEPOSIT_SUCCESS("deposit_success", "Deposit Successful"),

    /**
     * Successful withdrawal transaction
     */
    WITHDRAWAL_SUCCESS("withdrawal_success", "Withdrawal Successful"),

    /**
     * Transfer authorization created
     */
    TRANSFER_AUTHORIZED("transfer_authorized", "Transfer Authorized"),

    /**
     * Transfer captured (funds debited from source)
     */
    TRANSFER_CAPTURED("transfer_captured", "Transfer Captured"),

    /**
     * Transfer settled (funds credited to destination)
     */
    TRANSFER_SETTLED("transfer_settled", "Transfer Completed"),

    /**
     * Transfer cancelled
     */
    TRANSFER_CANCELLED("transfer_cancelled", "Transfer Cancelled"),

    /**
     * Account hold placed
     */
    ACCOUNT_HOLD_PLACED("account_hold_placed", "Funds on Hold"),

    /**
     * Account hold released
     */
    ACCOUNT_HOLD_RELEASED("account_hold_released", "Funds Released"),

    /**
     * Transaction failed
     */
    TRANSACTION_FAILED("transaction_failed", "Transaction Failed"),

    /**
     * Low balance warning
     */
    LOW_BALANCE_WARNING("low_balance_warning", "Low Balance Alert"),

    /**
     * Large transaction alert
     */
    LARGE_TRANSACTION_ALERT("large_transaction_alert", "Large Transaction Alert");

    /**
     * Template identifier used to locate the appropriate notification template
     */
    private final String templateId;

    /**
     * Human-readable subject/title for the notification
     */
    private final String subject;
}
