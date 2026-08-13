package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Plain lookup — users collection stays uninitialized.
     */
    Optional<Account> findById(Long id);

    /**
     * Fetches the account with its users initialized in one query.
     */
    @EntityGraph(attributePaths = {"users"})
    Optional<Account> findWithUsersById(Long id);
}