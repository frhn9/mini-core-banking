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
 * Push notification implementation of NotificationChannel.
 * Sends push notifications to mobile devices using Firebase Cloud Messaging (FCM).
 *
 * NOTE: This is a mock implementation that logs push notifications.
 * To enable actual push notifications:
 * 1. Add Firebase Admin SDK to build.gradle: implementation 'com.google.firebase:firebase-admin:9.x.x'
 * 2. Download service account JSON from Firebase Console
 * 3. Configure Firebase in application.yml
 * 4. Uncomment the FCM integration code below
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "notification.channels.push",
        name = "enabled",
        havingValue = "true"
)
public class PushNotificationChannel implements NotificationChannel {

    private final NotificationTemplateResolver templateResolver;

    @Value("${notification.channels.push.provider:mock}")
    private String provider;

    // Firebase configuration (uncomment when Firebase is added)
    // @Value("${notification.channels.push.firebase.credentials-path:}")
    // private String firebaseCredentialsPath;
    //
    // private FirebaseApp firebaseApp;
    //
    // @PostConstruct
    // public void initialize() {
    //     try {
    //         FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);
    //         FirebaseOptions options = FirebaseOptions.builder()
    //                 .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    //                 .build();
    //         firebaseApp = FirebaseApp.initializeApp(options);
    //         log.info("Firebase initialized successfully");
    //     } catch (IOException e) {
    //         log.error("Failed to initialize Firebase: {}", e.getMessage());
    //     }
    // }

    @Override
    public void send(NotificationData notificationData) {
        String message = templateResolver.resolvePushTemplate(
                notificationData.getEvent(),
                notificationData.getVariables()
        );

        String deviceToken = notificationData.getRecipient().getDeviceToken();
        String title = notificationData.getSubject();

        if ("mock".equalsIgnoreCase(provider)) {
            sendMockPush(deviceToken, title, message);
        } else if ("fcm".equalsIgnoreCase(provider) || "firebase".equalsIgnoreCase(provider)) {
            sendFcmPush(deviceToken, title, message);
        } else {
            log.warn("Unknown push provider: {}. Falling back to mock.", provider);
            sendMockPush(deviceToken, title, message);
        }
    }

    private void sendMockPush(String deviceToken, String title, String message) {
        log.info("============ MOCK PUSH NOTIFICATION ============");
        log.info("Device Token: {}", deviceToken);
        log.info("Title: {}", title);
        log.info("Message: {}", message);
        log.info("================================================");
    }

    private void sendFcmPush(String deviceToken, String title, String message) {
        // TODO: Implement FCM integration
        // try {
        //     Message fcmMessage = Message.builder()
        //             .setNotification(Notification.builder()
        //                     .setTitle(title)
        //                     .setBody(message)
        //                     .build())
        //             .setToken(deviceToken)
        //             .build();
        //
        //     String response = FirebaseMessaging.getInstance(firebaseApp).send(fcmMessage);
        //     log.info("Push notification sent via FCM. Response: {}", response);
        // } catch (FirebaseMessagingException e) {
        //     log.error("Failed to send push notification via FCM: {}", e.getMessage());
        //     throw new NotificationException("Failed to send push notification", e);
        // }

        log.warn("FCM push provider is configured but not implemented. Add Firebase Admin SDK to enable.");
        throw new NotificationException("FCM push provider not implemented. Add Firebase Admin SDK dependency.");
    }

    @Override
    public org.fd.mcb.shared.notification.enums.NotificationChannel getChannelType() {
        return org.fd.mcb.shared.notification.enums.NotificationChannel.PUSH;
    }

    @Override
    public boolean isEnabled() {
        return true;  // Enabled if component is loaded
    }

    @Override
    public boolean canSend(NotificationData notificationData) {
        return notificationData != null
                && notificationData.getRecipient() != null
                && notificationData.getRecipient().hasDeviceToken();
    }
}
