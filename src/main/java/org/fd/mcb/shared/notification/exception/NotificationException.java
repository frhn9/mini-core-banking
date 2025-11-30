package org.fd.mcb.shared.notification.exception;

/**
 * Exception thrown when a notification fails to send.
 * This is a runtime exception to avoid forcing callers to handle it,
 * as notification failures should not interrupt business transactions.
 */
public class NotificationException extends RuntimeException {

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationException(Throwable cause) {
        super(cause);
    }
}
