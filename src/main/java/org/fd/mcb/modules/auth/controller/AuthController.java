package org.fd.mcb.modules.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.modules.auth.dto.request.LoginRequest;
import org.fd.mcb.modules.auth.dto.request.RefreshTokenRequest;
import org.fd.mcb.modules.auth.dto.request.LogoutRequest;
import org.fd.mcb.modules.auth.dto.response.AuthResponse;
import org.fd.mcb.modules.auth.dto.response.RefreshTokenResponse;
import org.fd.mcb.modules.auth.service.AuthenticationService;
import org.fd.mcb.shared.response.ResponseEnum;
import org.fd.mcb.shared.response.ResponseHelper;
import org.fd.mcb.shared.response.ResponseMessageHelper;
import org.fd.mcb.shared.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller handling login, logout, and token refresh operations.
 * Provides unified authentication endpoints for both staff and customers.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final ResponseHelper responseHelper;

    /**
     * User login endpoint.
     * Accepts credentials for both staff (username) and customers (CIN).
     *
     * @param request LoginRequest with identifier, password, and userType
     * @param httpRequest HTTP request for session metadata
     * @return AuthResponse with JWT tokens and user information
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Login attempt for identifier: {}", request.getIdentifier());

        AuthResponse authResponse = authenticationService.login(request, httpRequest);

        log.info("Login successful for identifier: {}", request.getIdentifier());

        return responseHelper.createResponseData(ResponseEnum.SUCCESS, authResponse);
    }

    /**
     * Refresh access token endpoint.
     * Validates the refresh token and returns a new access token.
     *
     * @param request RefreshTokenRequest with the refresh token
     * @param httpRequest HTTP request for session metadata
     * @return RefreshTokenResponse with new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        log.debug("Token refresh request received");

        RefreshTokenResponse refreshResponse = authenticationService.refreshToken(request, httpRequest);

        log.debug("Token refresh successful");

        return responseHelper.createResponseData(ResponseEnum.TOKEN_REFRESHED, refreshResponse);
    }

    /**
     * Logout endpoint.
     * Revokes the refresh token and ends the current session.
     *
     * @param request LogoutRequest with the refresh token to revoke
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        log.info("Logout request received");

        authenticationService.logout(request);

        return responseHelper.createResponseData(ResponseEnum.LOGOUT_SUCCESS, null);
    }

    /**
     * Logout from all sessions endpoint.
     * Revokes all refresh tokens and ends all sessions for the current user.
     *
     * @param authentication current authenticated user
     * @return Success message
     */
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        log.info("Logout all sessions request for user: {}", userPrincipal.getUsername());

        authenticationService.logoutAll(userPrincipal);

        return responseHelper.createResponseData(ResponseEnum.LOGOUT_SUCCESS, null);
    }

    /**
     * Health check endpoint for authentication service.
     * 
     * @return Success message indicating authentication service is running
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return responseHelper.createResponseData(ResponseEnum.SUCCESS,
                "Authentication service is running");
    }
}
