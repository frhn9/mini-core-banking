package org.fd.mcb.shared.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.fd.mcb.shared.enums.UserType;
import org.fd.mcb.shared.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for generating and validating JWT tokens.
 * Handles both access tokens (short-lived) and refresh tokens (long-lived).
 */
@Slf4j
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
    }

    /**
     * Generates an access token for the given user.
     *
     * @param userPrincipal the authenticated user
     * @return JWT access token string
     */
    public String generateAccessToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getUserId());
        claims.put("userType", userPrincipal.getUserType().name());
        claims.put("role", userPrincipal.getRole());

        return generateToken(
                claims,
                userPrincipal.getUsername(),
                accessTokenExpiration,
                generateJti()
        );
    }

    /**
     * Generates a refresh token for the given user.
     *
     * @param userPrincipal the authenticated user
     * @return JWT refresh token string
     */
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getUserId());
        claims.put("userType", userPrincipal.getUserType().name());

        return generateToken(
                claims,
                userPrincipal.getUsername(),
                refreshTokenExpiration,
                null  // Refresh tokens don't need JTI for session tracking
        );
    }

    /**
     * Generates a JWT token with the given parameters.
     *
     * @param claims    custom claims to include
     * @param subject   subject (username/CIN)
     * @param expiration expiration time in milliseconds
     * @param jti       JWT ID (optional)
     * @return JWT token string
     */
    private String generateToken(
            Map<String, Object> claims,
            String subject,
            long expiration,
            String jti
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey);

        if (jti != null) {
            builder.id(jti);
        }

        return builder.compact();
    }

    /**
     * Validates a JWT token.
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token
     * @return Claims object
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the username/CIN from a JWT token.
     *
     * @param token the JWT token
     * @return username or CIN
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the user type from a JWT token.
     *
     * @param token the JWT token
     * @return UserType (STAFF or CUSTOMER)
     */
    public UserType extractUserType(String token) {
        String userType = extractClaims(token).get("userType", String.class);
        return UserType.valueOf(userType);
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT token
     * @return user ID
     */
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    /**
     * Extracts the JWT ID (jti) from a JWT token.
     *
     * @param token the JWT token
     * @return JWT ID, or null if not present
     */
    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a unique JWT ID (jti) for session tracking.
     *
     * @return unique JWT ID
     */
    public String generateJti() {
        return UUID.randomUUID().toString();
    }

    /**
     * Gets the access token expiration time in milliseconds.
     *
     * @return expiration time
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Gets the refresh token expiration time in milliseconds.
     *
     * @return expiration time
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
