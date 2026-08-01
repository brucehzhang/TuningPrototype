package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Plain lookup — account proxy and experiments collection stay uninitialized.
     */
    Optional<User> findById(Long id);

    /**
     * Fetches the user with its account initialized.
     */
    @EntityGraph(attributePaths = {"account"})
    Optional<User> findWithAccountById(Long id);

    /**
     * Fetches the user with its created experiments initialized.
     */
    @EntityGraph(attributePaths = {"experiments"})
    Optional<User> findWithExperimentsById(Long id);

    /**
     * Fetches the user with both account and experiments initialized.
     */
    @EntityGraph(attributePaths = {"account", "experiments"})
    Optional<User> findFullyHydratedById(Long id);

    /**
     * All users for a given account, with the account itself initialized
     * so it isn't re-fetched per row.
     */
    @EntityGraph(attributePaths = {"account"})
    List<User> findWithAccountByAccount_Id(Long accountId);
}