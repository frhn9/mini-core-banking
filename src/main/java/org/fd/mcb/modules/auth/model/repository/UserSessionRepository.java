package org.fd.mcb.modules.auth.model.repository;

import org.fd.mcb.modules.auth.model.entity.UserSession;
import org.fd.mcb.shared.enums.UserType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing UserSession entities.
 */
@Repository
public interface UserSessionRepository extends CrudRepository<UserSession, Long> {

    /**
     * Find a session by its unique session ID.
     *
     * @param sessionId the session ID
     * @return Optional containing the UserSession if found
     */
    Optional<UserSession> findBySessionId(String sessionId);

    /**
     * Find a session by access token JTI.
     *
     * @param jti the JWT ID from the access token
     * @return Optional containing the UserSession if found
     */
    Optional<UserSession> findByAccessTokenJti(String jti);

    /**
     * Find all sessions for a specific user.
     *
     * @param userType the type of user
     * @param userId   the user's ID
     * @return list of user sessions
     */
    List<UserSession> findByUserTypeAndUserId(UserType userType, Long userId);

    /**
     * Find all active sessions for a specific user.
     *
     * @param userType the type of user
     * @param userId   the user's ID
     * @param active   whether the session is active
     * @return list of active user sessions
     */
    List<UserSession> findByUserTypeAndUserIdAndActive(UserType userType, Long userId, Boolean active);

    /**
     * Find all expired sessions that are still marked as active.
     *
     * @param now current timestamp
     * @return list of expired sessions
     */
    List<UserSession> findByActiveAndExpiresAtBefore(Boolean active, ZonedDateTime now);

    /**
     * Delete all inactive sessions that expired before a certain time.
     * Used for cleanup of old sessions.
     *
     * @param active  activity status (false for inactive)
     * @param expiredBefore timestamp before which inactive sessions should be deleted
     */
    void deleteByActiveAndExpiresAtBefore(Boolean active, ZonedDateTime expiredBefore);
}
