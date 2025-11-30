package org.fd.mcb.modules.auth.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.auth.model.repository.RefreshTokenRepository;
import org.fd.mcb.modules.auth.service.SessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Scheduled job for cleaning up expired sessions and refresh tokens.
 * Runs every hour to maintain database hygiene and security.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionCleanupJob {

    private final SessionService sessionService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Clean up expired sessions and refresh tokens.
     * Runs every hour at minute 0.
     * 
     * This job:
     * 1. Marks expired sessions as inactive
     * 2. Marks expired refresh tokens as revoked
     * 3. Removes old inactive records to keep database clean
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    @Transactional
    public void cleanupExpiredSessionsAndTokens() {
        log.info("Starting scheduled cleanup of expired sessions and refresh tokens");

        try {
            // Clean up expired sessions
            sessionService.cleanupExpiredSessions();

            // Clean up expired refresh tokens
            cleanupExpiredRefreshTokens();

            log.info("Scheduled cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Clean up expired refresh tokens.
     */
    @Transactional
    public void cleanupExpiredRefreshTokens() {
        ZonedDateTime now = ZonedDateTime.now();

        // Mark expired tokens as revoked (if not already)
        refreshTokenRepository.findByExpiresAtBefore(now)
                .stream()
                .filter(token -> !token.getRevoked())
                .forEach(token -> {
                    token.setRevoked(true);
                    log.debug("Marked expired refresh token as revoked: {}", token.getId());
                });

        refreshTokenRepository.saveAll(
                refreshTokenRepository.findByExpiresAtBefore(now)
                        .stream()
                        .filter(token -> !token.getRevoked())
                        .toList()
        );

        // Clean up old revoked tokens (older than 30 days)
        ZonedDateTime cutoffDate = now.minusDays(30);
        int deletedCount = refreshTokenRepository.deleteByRevokedAndExpiresAtBefore(true, cutoffDate).size();

        log.info("Cleaned up {} expired refresh tokens", deletedCount);
    }

    /**
     * Daily cleanup job for more thorough maintenance.
     * Runs at 2:00 AM every day.
     */
    @Scheduled(cron = "0 0 2 * * *") // Every day at 2:00 AM
    @Transactional
    public void dailyMaintenance() {
        log.info("Starting daily maintenance cleanup");

        // Perform more thorough cleanup
        cleanupExpiredSessionsAndTokens();

        // Additional maintenance tasks can be added here
        log.info("Daily maintenance completed");
    }
}
