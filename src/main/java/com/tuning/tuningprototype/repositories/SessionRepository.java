package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     * Looks up a session by its opaque token, used to authenticate an incoming request.
     */
    Optional<Session> findBySessionToken(String sessionToken);

    /**
     * All sessions belonging to a given user, e.g. to list or revoke a user's active sessions.
     */
    List<Session> findByUserId(Long userId);
}
