package org.fd.mcb.shared.notification.annotation;

import org.fd.mcb.shared.notification.enums.NotificationChannel;
import org.fd.mcb.shared.notification.enums.NotificationEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to trigger notifications after successful method execution.
 *
 * This annotation uses AOP to intercept method calls and send notifications
 * through configured channels. Notifications are sent asynchronously after
 * the annotated method completes successfully.
 *
 * Usage example:
 * <pre>
 * {@code
 * @Notify(
 *   event = NotificationEvent.DEPOSIT_SUCCESS,
 *   channels = {NotificationChannel.EMAIL, NotificationChannel.SMS}
 * )
 * public AccountResponse deposit(DepositWithdrawReq request) {
 *   // method implementation
 * }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Notify {

    /**
     * The event type that determines which notification template to use.
     * This is mapped to specific message templates for each channel.
     *
     * @return the notification event type
     */
    NotificationEvent event();

    /**
     * The notification channels to use for sending the notification.
     * Multiple channels can be specified to send the same notification
     * through different mediums (e.g., both email and SMS).
     *
     * Default: EMAIL channel only
     *
     * @return array of notification channels
     */
    NotificationChannel[] channels() default {NotificationChannel.EMAIL};

    /**
     * Whether to send notifications asynchronously.
     * When true, notifications are sent in background threads without blocking
     * the main transaction. When false, notifications are sent synchronously.
     *
     * Default: true (asynchronous)
     *
     * @return true if notifications should be sent asynchronously
     */
    boolean async() default true;

    /**
     * Custom description for logging and debugging purposes.
     * This helps identify the purpose of the notification in logs.
     *
     * @return description text
     */
    String description() default "";
}
