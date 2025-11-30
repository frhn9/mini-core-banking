package org.fd.mcb.modules.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.auth.model.entity.RefreshToken;
import org.fd.mcb.modules.auth.model.entity.UserSession;
import org.fd.mcb.modules.auth.model.repository.UserSessionRepository;
import org.fd.mcb.shared.enums.UserType;
import org.fd.mcb.shared.exception.SessionNotFoundException;
import org.fd.mcb.shared.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing user sessions.
 * Handles session creation, tracking, and cleanup operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository userSessionRepository;

    /**
     * Generates a unique session ID.
     *
     * @return unique session identifier
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Creates a new user session.
     *
     * @param userPrincipal the authenticated user
     * @param refreshToken the refresh token for this session
     * @param jti the JWT ID from the access token
     * @param sessionId unique session identifier
     * @param deviceInfo device information
     * @param ipAddress IP address
     * @param userAgent user agent string
     * @return created UserSession entity
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserSession createSession(
            UserPrincipal userPrincipal,
            RefreshToken refreshToken,
            String jti,
            String sessionId,
            String deviceInfo,
            String ipAddress,
            String userAgent
    ) {
        ZonedDateTime expiresAt = ZonedDateTime.now().plusDays(7); // Session expires with refresh token

        UserSession session = UserSession.builder()
                .userType(userPrincipal.getUserType())
                .userId(userPrincipal.getUserId())
                .sessionId(sessionId)
                .accessTokenJti(jti)
                .refreshToken(refreshToken)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .active(true)
                .build();

        return userSessionRepository.save(session);
    }

    /**
     * Updates the session activity timestamp.
     *
     * @param jti the JWT ID from the access token
     */
    @Transactional
    public void updateSessionActivity(String jti) {
        UserSession session = userSessionRepository.findByAccessTokenJti(jti)
                .orElseThrow(() -> {
                    log.warn("Session not found for JTI: {}", jti);
                    return new SessionNotFoundException();
                });

        session.setLastActivityAt(ZonedDateTime.now());
        userSessionRepository.save(session);
    }

    /**
     * Updates session activity by refresh token.
     *
     * @param refreshToken the refresh token
     * @param jti the new JWT ID
     */
    @Transactional
    public void updateSessionActivityByRefreshToken(RefreshToken refreshToken, String jti) {
        List<UserSession> sessions = userSessionRepository
                .findByUserTypeAndUserIdAndActive(refreshToken.getUserType(), refreshToken.getUserId(), true);

        sessions.stream()
                .filter(session -> session.getRefreshToken() != null && 
                                session.getRefreshToken().getId().equals(refreshToken.getId()))
                .findFirst()
                .ifPresent(session -> {
                    session.setAccessTokenJti(jti);
                    session.setLastActivityAt(ZonedDateTime.now());
                    userSessionRepository.save(session);
                });
    }

    /**
     * Gets all active sessions for a user.
     *
     * @param userPrincipal the user
     * @return list of active user sessions
     */
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(UserPrincipal userPrincipal) {
        return userSessionRepository.findByUserTypeAndUserIdAndActive(
                userPrincipal.getUserType(),
                userPrincipal.getUserId(),
                true
        );
    }

    /**
     * Revokes a specific session.
     *
     * @param userPrincipal the user
     * @param sessionId the session ID to revoke
     * @throws SessionNotFoundException if session not found
     */
    @Transactional
    public void revokeSession(UserPrincipal userPrincipal, String sessionId) {
        UserSession session = userSessionRepository.findBySessionId(sessionId)
                .orElseThrow(SessionNotFoundException::new);

        // Verify the session belongs to the user
        if (!session.getUserType().equals(userPrincipal.getUserType()) ||
            !session.getUserId().equals(userPrincipal.getUserId())) {
            throw new SessionNotFoundException();
        }

        session.setActive(false);
        userSessionRepository.save(session);

        log.info("Session revoked: {} for user: {}", sessionId, userPrincipal.getUsername());
    }

    /**
     * Revokes a session by refresh token.
     *
     * @param refreshToken the refresh token
     */
    @Transactional
    public void revokeSessionByRefreshToken(RefreshToken refreshToken) {
        List<UserSession> sessions = userSessionRepository
                .findByUserTypeAndUserIdAndActive(refreshToken.getUserType(), refreshToken.getUserId(), true);

        sessions.stream()
                .filter(session -> session.getRefreshToken() != null && 
                                session.getRefreshToken().getId().equals(refreshToken.getId()))
                .forEach(session -> {
                    session.setActive(false);
                    userSessionRepository.save(session);
                });

        log.info("Sessions revoked for refresh token: {}", refreshToken.getId());
    }

    /**
     * Revokes all sessions for a user.
     *
     * @param userPrincipal the user
     */
    @Transactional
    public void revokeAllSessions(UserPrincipal userPrincipal) {
        List<UserSession> sessions = getActiveSessions(userPrincipal);

        sessions.forEach(session -> session.setActive(false));
        userSessionRepository.saveAll(sessions);

        log.info("All sessions revoked for user: {}", userPrincipal.getUsername());
    }

    /**
     * Gets the current session ID for a user and JTI.
     *
     * @param userPrincipal the user
     * @param jti the JWT ID
     * @return session ID, or null if not found
     */
    @Transactional(readOnly = true)
    public String getCurrentSessionId(UserPrincipal userPrincipal, String jti) {
        return userSessionRepository.findByAccessTokenJti(jti)
                .filter(session -> session.getUserType().equals(userPrincipal.getUserType()) &&
                                  session.getUserId().equals(userPrincipal.getUserId()))
                .map(UserSession::getSessionId)
                .orElse(null);
    }

    /**
     * Cleans up expired sessions.
     * This method is typically called by a scheduled job.
     */
    @Transactional
    public void cleanupExpiredSessions() {
        ZonedDateTime now = ZonedDateTime.now();
        
        // Mark expired sessions as inactive
        List<UserSession> expiredSessions = userSessionRepository
                .findByActiveAndExpiresAtBefore(true, now);
        
        expiredSessions.forEach(session -> session.setActive(false));
        userSessionRepository.saveAll(expiredSessions);

        // Clean up old inactive sessions (older than 30 days)
        ZonedDateTime cutoffDate = now.minusDays(30);
        userSessionRepository.deleteByActiveAndExpiresAtBefore(false, cutoffDate);

        log.info("Cleaned up {} expired sessions", expiredSessions.size());
    }
}
