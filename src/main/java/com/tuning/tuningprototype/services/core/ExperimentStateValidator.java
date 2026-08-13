package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Narrow, read-only component for validating experiment lifecycle state.
 * Depends only on ExperimentRepository — deliberately NOT on ExperimentService —
 * so that other services (WalletService, SampleService, etc.) can check experiment
 * editability without creating a circular service dependency with ExperimentService.
 */
@Component
public class ExperimentStateValidator {

    private final ExperimentRepository experimentRepository;

    public ExperimentStateValidator(ExperimentRepository experimentRepository) {
        this.experimentRepository = experimentRepository;
    }

    public ExperimentStatus getStatus(Long experimentId) {
        return experimentRepository.findById(experimentId)
                .map(Experiment::getExperimentStatus)
                .orElseThrow(() -> new EntityNotFoundException("Experiment not found: " + experimentId));
    }
}