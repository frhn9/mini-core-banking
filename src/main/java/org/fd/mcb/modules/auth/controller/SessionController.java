package org.fd.mcb.modules.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.auth.dto.response.UserSessionResponse;
import org.fd.mcb.modules.auth.service.SessionService;
import org.fd.mcb.modules.auth.model.entity.UserSession;
import org.fd.mcb.modules.auth.model.mapper.UserSessionMapper;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Session management controller.
 * Provides endpoints for viewing and managing active user sessions.
 */
@Slf4j
@RestController
@RequestMapping("/auth/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final ResponseHelper responseHelper;
    private final UserSessionMapper userSessionMapper;

    /**
     * Get all active sessions for the authenticated user.
     *
     * @param authentication current authenticated user
     * @return List of active user sessions
     */
    @GetMapping
    public ResponseEntity<?> getActiveSessions(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.info("Get active sessions request for user: {}", userPrincipal.getUsername());

        List<UserSession> activeSessions = sessionService.getActiveSessions(userPrincipal);
        
        List<UserSessionResponse> sessionResponses = activeSessions.stream()
                .map(userSessionMapper::toResponse)
                .toList();

        log.info("Found {} active sessions for user: {}", sessionResponses.size(), userPrincipal.getUsername());

        return responseHelper.createResponseData(ResponseEnum.SUCCESS, sessionResponses);
    }

    /**
     * Get session details by session ID.
     *
     * @param sessionId the session ID
     * @param authentication current authenticated user
     * @return Session details
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getSessionById(@PathVariable String sessionId, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.debug("Get session details request: {} for user: {}", sessionId, userPrincipal.getUsername());

        List<UserSession> activeSessions = sessionService.getActiveSessions(userPrincipal);
        
        UserSession session = activeSessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst()
                .orElse(null);

        if (session == null) {
            return responseHelper.createResponseError("Session not found", "session.not.found");
        }

        UserSessionResponse response = userSessionMapper.toResponse(session);

        return responseHelper.createResponseData(ResponseEnum.SUCCESS, response);
    }

    /**
     * Revoke a specific session by session ID.
     *
     * @param sessionId the session ID to revoke
     * @param authentication current authenticated user
     * @return Success message
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> revokeSession(@PathVariable String sessionId, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.info("Revoke session request: {} for user: {}", sessionId, userPrincipal.getUsername());

        sessionService.revokeSession(userPrincipal, sessionId);

        return responseHelper.createResponseData(ResponseEnum.SUCCESS, 
                "Session revoked successfully");
    }

    /**
     * Mark current session as current (helper endpoint).
     * Returns the current session information with a flag indicating it's the active session.
     *
     * @param authentication current authenticated user
     * @return Current session information
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSession(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.debug("Get current session request for user: {}", userPrincipal.getUsername());

        List<UserSession> activeSessions = sessionService.getActiveSessions(userPrincipal);
        
        if (activeSessions.isEmpty()) {
            return responseHelper.createResponseError("No active sessions found", "session.active.not.found");
        }

        // Return the most recent session (assuming sessions are ordered by creation time)
        UserSession currentSession = activeSessions.get(0);
        UserSessionResponse response = userSessionMapper.toResponse(currentSession);
        
        return responseHelper.createResponseData(ResponseEnum.SUCCESS, response);
    }

    /**
     * Session statistics endpoint.
     * Returns basic statistics about the user's sessions.
     *
     * @param authentication current authenticated user
     * @return Session statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getSessionStats(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.debug("Get session stats request for user: {}", userPrincipal.getUsername());

        List<UserSession> activeSessions = sessionService.getActiveSessions(userPrincipal);

        // Simple statistics
        long totalSessions = activeSessions.size();
        long desktopSessions = activeSessions.stream()
                .filter(session -> "Desktop Browser".equals(session.getDeviceInfo()))
                .count();
        long mobileSessions = activeSessions.stream()
                .filter(session -> "Mobile Device".equals(session.getDeviceInfo()))
                .count();

        var stats = new Object() {
            public final long totalSessions = totalSessions;
            public final long desktopSessions = desktopSessions;
            public final long mobileSessions = mobileSessions;
            public final long otherSessions = totalSessions - desktopSessions - mobileSessions;
        };

        return responseHelper.createResponseData(ResponseEnum.SUCCESS, stats);
    }
}
