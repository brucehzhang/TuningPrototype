package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Plain lookup — experiments collection stays uninitialized.
     * No parent association to fetch since @ManyToOne to Account was removed.
     */
    Optional<User> findById(Long id);

    /**
     * Fetches the user with its created experiments initialized in one query.
     */
    @EntityGraph(attributePaths = {"experiments"})
    Optional<User> findWithExperimentsById(Long id);

    /**
     * All users belonging to a given account, via the plain FK column.
     */
    List<User> findByAccountId(Long accountId);
}