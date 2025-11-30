package org.fd.mcb.modules.auth.model.repository;

import org.fd.mcb.modules.auth.model.entity.RefreshToken;
import org.fd.mcb.shared.enums.UserType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing RefreshToken entities.
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    /**
     * Find a refresh token by its token string.
     *
     * @param token the token string
     * @return Optional containing the RefreshToken if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all refresh tokens for a specific user.
     *
     * @param userType the type of user (STAFF or CUSTOMER)
     * @param userId   the user's ID
     * @return list of refresh tokens
     */
    List<RefreshToken> findByUserTypeAndUserId(UserType userType, Long userId);

    /**
     * Find all active (not revoked) refresh tokens for a specific user.
     *
     * @param userType the type of user
     * @param userId   the user's ID
     * @param revoked  revocation status (false for active)
     * @return list of active refresh tokens
     */
    List<RefreshToken> findByUserTypeAndUserIdAndRevoked(UserType userType, Long userId, Boolean revoked);

    /**
     * Find all expired tokens that haven't been cleaned up yet.
     *
     * @param now current timestamp
     * @return list of expired tokens
     */
    List<RefreshToken> findByExpiresAtBefore(ZonedDateTime now);

    /**
     * Delete all revoked tokens that expired before a certain time.
     * Used for cleanup of old tokens.
     *
     * @param revokedBefore timestamp before which revoked tokens should be deleted
     */
    void deleteByRevokedAndExpiresAtBefore(Boolean revoked, ZonedDateTime revokedBefore);
}
