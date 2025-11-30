package org.fd.mcb.shared.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.notification.channel.NotificationChannel;
import org.fd.mcb.shared.notification.dto.NotificationData;
import org.fd.mcb.shared.notification.service.NotificationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default implementation of NotificationService.
 * Manages multiple notification channels and handles both sync and async delivery.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final List<NotificationChannel> notificationChannels;

    /**
     * Map of channel types to their implementations for quick lookup
     */
    private Map<org.fd.mcb.shared.notification.enums.NotificationChannel, NotificationChannel> channelMap;

    /**
     * Initializes the channel map after dependency injection
     */
    private Map<org.fd.mcb.shared.notification.enums.NotificationChannel, NotificationChannel> getChannelMap() {
        if (channelMap == null) {
            channelMap = notificationChannels.stream()
                    .collect(Collectors.toMap(
                            NotificationChannel::getChannelType,
                            Function.identity()
                    ));
        }
        return channelMap;
    }

    @Override
    public void send(NotificationData notificationData, Set<org.fd.mcb.shared.notification.enums.NotificationChannel> channels) {
        if (notificationData == null || channels == null || channels.isEmpty()) {
            log.warn("Invalid notification request - data or channels are null/empty");
            return;
        }

        log.info("Sending notification for event: {} to {} channels",
                notificationData.getEvent(), channels.size());

        channels.forEach(channelType -> send(notificationData, channelType));
    }

    @Override
    public void send(NotificationData notificationData, org.fd.mcb.shared.notification.enums.NotificationChannel channelType) {
        NotificationChannel channel = getChannelMap().get(channelType);

        if (channel == null) {
            log.warn("No implementation found for channel: {}", channelType);
            return;
        }

        if (!channel.isEnabled()) {
            log.debug("Channel {} is disabled, skipping notification", channelType);
            return;
        }

        if (!channel.canSend(notificationData)) {
            log.warn("Channel {} cannot send notification - recipient missing required contact info", channelType);
            return;
        }

        try {
            log.debug("Sending notification via {} channel to recipient: {}",
                    channelType, notificationData.getRecipient().getName());
            channel.send(notificationData);
            log.info("Successfully sent notification via {} channel", channelType);
        } catch (Exception e) {
            log.error("Failed to send notification via {} channel: {}",
                    channelType, e.getMessage(), e);
            // Don't rethrow - notification failures should not break business logic
        }
    }

    @Async("virtualThreadExecutor")
    @Override
    public void sendAsync(NotificationData notificationData, Set<org.fd.mcb.shared.notification.enums.NotificationChannel> channels) {
        send(notificationData, channels);
    }

    @Async("virtualThreadExecutor")
    @Override
    public void sendAsync(NotificationData notificationData, org.fd.mcb.shared.notification.enums.NotificationChannel channel) {
        send(notificationData, channel);
    }
}
