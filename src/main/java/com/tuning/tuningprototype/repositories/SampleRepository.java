package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.entity.Sample;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SampleRepository extends JpaRepository<Sample, Long> {

    /**
     * Plain lookup — decisions collection stays uninitialized.
     * No parent association to fetch since @ManyToOne to Experiment was removed.
     */
    Optional<Sample> findById(Long id);

    /**
     * Fetches the sample with its decisions initialized in one query.
     */
    @EntityGraph(attributePaths = {"decisions"})
    Optional<Sample> findWithDecisionsById(Long id);

    /**
     * All samples for a given experiment, via the plain FK column.
     */
    List<Sample> findByExperimentId(Long experimentId);

    /**
     * All samples for a given experiment, with decisions initialized per row.
     */
    @EntityGraph(attributePaths = {"decisions"})
    List<Sample> findWithDecisionsByExperimentId(Long experimentId);
}