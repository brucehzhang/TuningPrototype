package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.Sample;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SampleRepository extends JpaRepository<Sample, Long> {

    /**
     * Plain lookup — experiment proxy and decisions collection stay uninitialized.
     */
    Optional<Sample> findById(Long id);

    /**
     * Fetches the sample with its parent experiment initialized.
     */
    @EntityGraph(attributePaths = {"experiment"})
    Optional<Sample> findWithExperimentById(Long id);

    /**
     * Fetches the sample with its decisions initialized.
     */
    @EntityGraph(attributePaths = {"decisions"})
    Optional<Sample> findWithDecisionsById(Long id);

    /**
     * Fetches the sample with both experiment and decisions initialized.
     */
    @EntityGraph(attributePaths = {"experiment", "decisions"})
    Optional<Sample> findFullyHydratedById(Long id);

    /**
     * All samples for a given experiment, with the experiment initialized.
     */
    @EntityGraph(attributePaths = {"experiment"})
    List<Sample> findWithExperimentByExperiment_Id(Long experimentId);
}