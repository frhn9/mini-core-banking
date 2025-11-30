package org.fd.mcb.shared.notification.template;

import org.fd.mcb.shared.notification.enums.NotificationEvent;

import java.util.Map;

/**
 * Resolves and populates notification templates for different channels.
 * Templates can be loaded from files, database, or defined inline.
 */
public interface NotificationTemplateResolver {

    /**
     * Resolves an email template and populates it with variables.
     *
     * @param event the notification event type
     * @param variables the template variables
     * @return HTML content for the email
     */
    String resolveEmailTemplate(NotificationEvent event, Map<String, Object> variables);

    /**
     * Resolves an SMS template and populates it with variables.
     *
     * @param event the notification event type
     * @param variables the template variables
     * @return Plain text content for the SMS
     */
    String resolveSmsTemplate(NotificationEvent event, Map<String, Object> variables);

    /**
     * Resolves a push notification template and populates it with variables.
     *
     * @param event the notification event type
     * @param variables the template variables
     * @return Plain text content for the push notification
     */
    String resolvePushTemplate(NotificationEvent event, Map<String, Object> variables);
}
