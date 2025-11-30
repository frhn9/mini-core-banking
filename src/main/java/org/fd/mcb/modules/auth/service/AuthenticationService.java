package org.fd.mcb.modules.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.auth.dto.request.LoginRequest;
import org.fd.mcb.modules.auth.dto.request.RefreshTokenRequest;
import org.fd.mcb.modules.auth.dto.request.LogoutRequest;
import org.fd.mcb.modules.auth.dto.response.AuthResponse;
import org.fd.mcb.modules.auth.dto.response.RefreshTokenResponse;
import org.fd.mcb.modules.auth.model.entity.RefreshToken;
import org.fd.mcb.modules.auth.model.entity.UserSession;
import org.fd.mcb.modules.auth.model.repository.RefreshTokenRepository;
import org.fd.mcb.modules.auth.model.repository.UserSessionRepository;
import org.fd.mcb.shared.enums.UserType;
import org.fd.mcb.shared.exception.*;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.security.UserPrincipal;
import org.fd.mcb.shared.security.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Service for handling authentication operations including login, token refresh, and logout.
 * Implements secure authentication with JWT tokens and session tracking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Authenticates a user with username/CIN and password.
     * Creates a new session and generates access/refresh tokens.
     *
     * @param request LoginRequest containing credentials
     * @param httpRequest HTTP request for session metadata
     * @return AuthResponse with tokens and user info
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws AccountLockedException if account is locked
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // Authenticate user credentials
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getIdentifier(),
                    request.getPassword()
            );
            
            var authentication = authenticationManager.authenticate(authToken);
            var userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Validate account status
            if (!userPrincipal.isAccountNonLocked()) {
                log.warn("Login attempt for locked account: {}", request.getIdentifier());
                throw new AccountLockedException();
            }

            if (!userPrincipal.isEnabled()) {
                log.warn("Login attempt for disabled account: {}", request.getIdentifier());
                throw new InvalidCredentialsException();
            }

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(userPrincipal);
            String refreshToken = jwtService.generateRefreshToken(userPrincipal);
            String jti = jwtService.extractJti(accessToken);
            String sessionId = sessionService.generateSessionId();

            // Extract device and request information
            String deviceInfo = extractDeviceInfo(httpRequest);
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            // Create refresh token record
            RefreshToken refreshTokenEntity = createRefreshTokenEntity(
                    userPrincipal,
                    refreshToken,
                    deviceInfo,
                    ipAddress
            );

            // Create user session
            UserSession userSession = sessionService.createSession(
                    userPrincipal,
                    refreshTokenEntity,
                    jti,
                    sessionId,
                    deviceInfo,
                    ipAddress,
                    userAgent
            );

            log.info("User logged in successfully: {} (session: {})", 
                    request.getIdentifier(), sessionId);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userType(userPrincipal.getUserType())
                    .identifier(userPrincipal.getUsername())
                    .role(userPrincipal.getRole())
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for identifier: {}", request.getIdentifier());
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Refreshes an access token using a valid refresh token.
     *
     * @param request RefreshTokenRequest containing refresh token
     * @param httpRequest HTTP request
     * @return RefreshTokenResponse with new access token
     * @throws RefreshTokenNotFoundException if refresh token not found
     * @throws RefreshTokenExpiredException if refresh token is expired
     * @throws RefreshTokenRevokedException if refresh token was revoked
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String refreshTokenStr = request.getRefreshToken();

        // Find and validate refresh token
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found: {}", refreshTokenStr.substring(0, Math.min(20, refreshTokenStr.length())) + "...");
                    return new RefreshTokenNotFoundException();
                });

        // Validate token status
        if (refreshTokenEntity.isExpired()) {
            log.warn("Expired refresh token used: {}", refreshTokenStr.substring(0, Math.min(20, refreshTokenStr.length())) + "...");
            throw new RefreshTokenExpiredException();
        }

        if (refreshTokenEntity.getRevoked()) {
            log.warn("Revoked refresh token used: {}", refreshTokenStr.substring(0, Math.min(20, refreshTokenStr.length())) + "...");
            throw new RefreshTokenRevokedException();
        }

        // Extract user information from token
        String username = jwtService.extractUsername(refreshTokenStr);
        UserType userType = jwtService.extractUserType(refreshTokenStr);

        // Create temporary UserPrincipal for token generation
        // In real scenario, you'd fetch the user details again
        var userPrincipal = UserPrincipal.builder()
                .userId(refreshTokenEntity.getUserId())
                .username(username)
                .password("") // Not needed for token generation
                .userType(userType)
                .authorities(List.of()) // Should be loaded from database
                .accountNonLocked(true)
                .enabled(true)
                .build();

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(userPrincipal);
        String jti = jwtService.extractJti(newAccessToken);

        // Update session activity
        sessionService.updateSessionActivityByRefreshToken(refreshTokenEntity, jti);

        // Update refresh token last used timestamp
        refreshTokenEntity.setLastUsedAt(ZonedDateTime.now());
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("Access token refreshed for user: {}", username);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    /**
     * Logs out a user by revoking the refresh token and ending the session.
     *
     * @param request LogoutRequest containing refresh token
     * @throws RefreshTokenNotFoundException if refresh token not found
     */
    @Transactional
    public void logout(LogoutRequest request) {
        String refreshTokenStr = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(RefreshTokenNotFoundException::new);

        // Revoke refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // End associated sessions
        sessionService.revokeSessionByRefreshToken(refreshToken);

        log.info("User logged out successfully: {}", jwtService.extractUsername(refreshTokenStr));
    }

    /**
     * Logs out all sessions for the current user.
     *
     * @param userPrincipal the current authenticated user
     */
    @Transactional
    public void logoutAll(UserPrincipal userPrincipal) {
        // Revoke all refresh tokens for the user
        List<RefreshToken> userTokens = refreshTokenRepository
                .findByUserTypeAndUserIdAndRevoked(userPrincipal.getUserType(), userPrincipal.getUserId(), false);
        
        userTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(userTokens);

        // End all sessions
        sessionService.revokeAllSessions(userPrincipal);

        log.info("All sessions logged out for user: {}", userPrincipal.getUsername());
    }

    /**
     * Creates a RefreshToken entity with the provided information.
     */
    private RefreshToken createRefreshTokenEntity(
            UserPrincipal userPrincipal,
            String refreshToken,
            String deviceInfo,
            String ipAddress
    ) {
        ZonedDateTime expiresAt = ZonedDateTime.now()
                .plusMillis(jwtService.getRefreshTokenExpiration());

        return RefreshToken.builder()
                .userType(userPrincipal.getUserType())
                .userId(userPrincipal.getUserId())
                .token(refreshToken)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
    }

    /**
     * Extracts device information from the HTTP request.
     */
    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown Device";
        }

        // Simple device detection - could be enhanced with a proper device detection library
        if (userAgent.contains("Mobile")) {
            return "Mobile Device";
        } else if (userAgent.contains("Tablet")) {
            return "Tablet Device";
        } else {
            return "Desktop Browser";
        }
    }

    /**
     * Extracts the client IP address from the HTTP request.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
