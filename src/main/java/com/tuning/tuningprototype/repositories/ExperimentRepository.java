package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.Experiment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperimentRepository extends JpaRepository<Experiment, Long> {

    /**
     * Plain lookup — createdByUser proxy, samples, wallets all stay uninitialized.
     */
    Optional<Experiment> findById(Long id);

    /**
     * Fetches the experiment with its creating user initialized.
     */
    @EntityGraph(attributePaths = {"createdByUser"})
    Optional<Experiment> findWithCreatedByUserById(Long id);

    /**
     * Fetches the experiment with its samples initialized.
     */
    @EntityGraph(attributePaths = {"samples"})
    Optional<Experiment> findWithSamplesById(Long id);

    /**
     * Fetches the experiment with its wallets initialized.
     */
    @EntityGraph(attributePaths = {"wallets"})
    Optional<Experiment> findWithWalletsById(Long id);

    /**
     * Fetches the experiment with samples and wallets initialized (no user).
     */
    @EntityGraph(attributePaths = {"samples", "wallets"})
    Optional<Experiment> findWithSamplesAndWalletsById(Long id);

    /**
     * Fetches the experiment with everything initialized.
     */
    @EntityGraph(attributePaths = {"createdByUser", "samples", "wallets"})
    Optional<Experiment> findFullyHydratedById(Long id);

    /**
     * All experiments created by a given user, with the user initialized.
     */
    @EntityGraph(attributePaths = {"createdByUser"})
    List<Experiment> findWithCreatedByUserByCreatedByUser_Id(Long userId);
}