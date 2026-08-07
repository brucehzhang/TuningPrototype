package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;

import java.util.Optional;

public interface IExperimentService {

    Optional<ExperimentDto> getExperiment(long id);

    ExperimentDto createExperiment(CreateExperimentRequest createExperimentRequest, long createdUserId);

    ExperimentDto updateExperiment(UpdateExperimentRequest updateExperimentRequest);
}
