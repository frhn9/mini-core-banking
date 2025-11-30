package org.fd.mcb.modules.auth.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fd.mcb.shared.enums.UserType;

import java.time.ZonedDateTime;

/**
 * Entity representing a JWT refresh token.
 * Refresh tokens are long-lived tokens used to obtain new access tokens
 * without requiring the user to re-authenticate.
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of user (STAFF or CUSTOMER) that owns this token
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    /**
     * ID of the user (Staff.id or Customer.id) that owns this token
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The actual refresh token string (JWT)
     */
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    /**
     * Device information (e.g., "iPhone 14 Pro")
     */
    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    /**
     * IP address from which the token was created
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * Expiration timestamp of the token
     */
    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;

    /**
     * Whether the token has been revoked (logged out)
     */
    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    /**
     * Timestamp when the token was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    /**
     * Timestamp when the token was last used to refresh an access token
     */
    @Column(name = "last_used_at")
    private ZonedDateTime lastUsedAt;

    /**
     * Lifecycle hook to set created timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    /**
     * Check if the token is expired
     */
    public boolean isExpired() {
        return ZonedDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if the token is valid (not revoked and not expired)
     */
    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
