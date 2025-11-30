package org.fd.mcb.modules.auth.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fd.mcb.shared.enums.UserType;

import java.time.ZonedDateTime;

/**
 * Entity representing an active user session.
 * Tracks session metadata including device information, IP address,
 * and last activity for security and session management purposes.
 */
@Entity
@Table(name = "user_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of user (STAFF or CUSTOMER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    /**
     * ID of the user (Staff.id or Customer.id)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Unique session identifier
     */
    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;

    /**
     * JWT ID (jti claim) of the current access token
     * Used to track and invalidate specific access tokens
     */
    @Column(name = "access_token_jti", unique = true, length = 255)
    private String accessTokenJti;

    /**
     * Reference to the refresh token associated with this session
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id", foreignKey = @ForeignKey(name = "fk_user_sessions_refresh_token_id"))
    private RefreshToken refreshToken;

    /**
     * Device information (e.g., "Chrome on Windows", "Safari on iPhone")
     */
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    /**
     * IP address from which the session was created
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * User agent string from the HTTP request
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Timestamp when the session was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Timestamp of the last activity in this session
     * Updated whenever the access token is used
     */
    @Column(name = "last_activity_at")
    private ZonedDateTime lastActivityAt;

    /**
     * Timestamp when the session expires
     * Typically tied to refresh token expiration
     */
    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    /**
     * Whether the session is currently active
     * Set to false when user logs out or session is revoked
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Lifecycle hook to set created timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        if (lastActivityAt == null) {
            lastActivityAt = createdAt;
        }
    }

    /**
     * Check if the session is expired
     */
    public boolean isExpired() {
        return expiresAt != null && ZonedDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if the session is valid (active and not expired)
     */
    public boolean isValid() {
        return active && !isExpired();
    }
}
