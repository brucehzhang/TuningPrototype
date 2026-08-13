package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;

// Mapper interface for experiments requests (create, update, etc)
public interface ExperimentRequestMapper {
    Experiment toEntity(CreateExperimentRequest request, Long createdUserId, Long nowEpochSeconds);
    void applyUpdate(UpdateExperimentRequest request, Experiment existing, Long nowEpochSeconds);
}