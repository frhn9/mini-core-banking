package org.fd.mcb.shared.notification.channel.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.notification.channel.NotificationChannel;
import org.fd.mcb.shared.notification.dto.NotificationData;
import org.fd.mcb.shared.notification.exception.NotificationException;
import org.fd.mcb.shared.notification.template.NotificationTemplateResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SMS implementation of NotificationChannel.
 * Sends SMS messages using third-party SMS provider (e.g., Twilio).
 *
 * NOTE: This is a mock implementation that logs SMS messages.
 * To enable actual SMS sending:
 * 1. Add Twilio SDK to build.gradle: implementation 'com.twilio.sdk:twilio:9.x.x'
 * 2. Configure Twilio credentials in application.yml
 * 3. Uncomment the Twilio integration code below
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "notification.channels.sms",
        name = "enabled",
        havingValue = "true"
)
public class SmsNotificationChannel implements NotificationChannel {

    private final NotificationTemplateResolver templateResolver;

    @Value("${notification.channels.sms.provider:mock}")
    private String provider;

    @Value("${notification.channels.sms.from:+1234567890}")
    private String fromNumber;

    // Twilio configuration (uncomment when Twilio is added)
    // @Value("${notification.channels.sms.twilio.account-sid:}")
    // private String twilioAccountSid;
    //
    // @Value("${notification.channels.sms.twilio.auth-token:}")
    // private String twilioAuthToken;

    @Override
    public void send(NotificationData notificationData) {
        String message = templateResolver.resolveSmsTemplate(
                notificationData.getEvent(),
                notificationData.getVariables()
        );

        String toNumber = notificationData.getRecipient().getPhoneNumber();

        if ("mock".equalsIgnoreCase(provider)) {
            sendMockSms(toNumber, message);
        } else if ("twilio".equalsIgnoreCase(provider)) {
            sendTwilioSms(toNumber, message);
        } else {
            log.warn("Unknown SMS provider: {}. Falling back to mock.", provider);
            sendMockSms(toNumber, message);
        }
    }

    private void sendMockSms(String toNumber, String message) {
        log.info("=============== MOCK SMS ===============");
        log.info("From: {}", fromNumber);
        log.info("To: {}", toNumber);
        log.info("Message: {}", message);
        log.info("========================================");
    }

    private void sendTwilioSms(String toNumber, String message) {
        // TODO: Implement Twilio integration
        // Twilio.init(twilioAccountSid, twilioAuthToken);
        //
        // try {
        //     Message twilioMessage = Message.creator(
        //             new PhoneNumber(toNumber),
        //             new PhoneNumber(fromNumber),
        //             message
        //     ).create();
        //
        //     log.info("SMS sent via Twilio. SID: {}", twilioMessage.getSid());
        // } catch (ApiException e) {
        //     log.error("Failed to send SMS via Twilio: {}", e.getMessage());
        //     throw new NotificationException("Failed to send SMS", e);
        // }

        log.warn("Twilio SMS provider is configured but not implemented. Add Twilio SDK to enable.");
        throw new NotificationException("Twilio SMS provider not implemented. Add Twilio SDK dependency.");
    }

    @Override
    public org.fd.mcb.shared.notification.enums.NotificationChannel getChannelType() {
        return org.fd.mcb.shared.notification.enums.NotificationChannel.SMS;
    }

    @Override
    public boolean isEnabled() {
        return true;  // Enabled if component is loaded
    }

    @Override
    public boolean canSend(NotificationData notificationData) {
        return notificationData != null
                && notificationData.getRecipient() != null
                && notificationData.getRecipient().hasPhoneNumber();
    }
}
