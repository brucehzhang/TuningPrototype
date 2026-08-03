package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.Experiment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperimentRepository extends JpaRepository<Experiment, Long> {

    /**
     * Plain lookup — samples and wallets collections stay uninitialized.
     * No parent association to fetch since @ManyToOne to User was removed.
     */
    Optional<Experiment> findById(Long id);

    /**
     * Fetches the experiment with its samples initialized in one query.
     */
    @EntityGraph(attributePaths = {"samples"})
    Optional<Experiment> findWithSamplesById(Long id);

    /**
     * Fetches the experiment with its wallets initialized in one query.
     */
    @EntityGraph(attributePaths = {"wallets"})
    Optional<Experiment> findWithWalletsById(Long id);

    /**
     * All experiments created by a given user, via the plain FK column.
     */
    List<Experiment> findByCreatedUserId(Long userId);
}