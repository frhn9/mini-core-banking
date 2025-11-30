package org.fd.mcb.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the notification system.
 * Maps to 'notification' prefix in application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private Channels channels = new Channels();

    @Data
    public static class Channels {
        private EmailConfig email = new EmailConfig();
        private SmsConfig sms = new SmsConfig();
        private PushConfig push = new PushConfig();
        private LogConfig log = new LogConfig();
    }

    @Data
    public static class EmailConfig {
        private boolean enabled = false;
        private String fromEmail = "noreply@bank.com";
        private String fromName = "Mini Core Banking";
    }

    @Data
    public static class SmsConfig {
        private boolean enabled = false;
        private String provider = "mock";  // mock, twilio
        private String from = "+1234567890";
        private TwilioConfig twilio = new TwilioConfig();
    }

    @Data
    public static class TwilioConfig {
        private String accountSid;
        private String authToken;
    }

    @Data
    public static class PushConfig {
        private boolean enabled = false;
        private String provider = "mock";  // mock, fcm, firebase
        private FirebaseConfig firebase = new FirebaseConfig();
    }

    @Data
    public static class FirebaseConfig {
        private String credentialsPath;
    }

    @Data
    public static class LogConfig {
        private boolean enabled = true;  // Enabled by default for development
    }
}
