package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.db.SampleDto;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;

import java.util.Optional;

public interface IExperimentService {

    Optional<ExperimentDto> getExperiment(long id);

    ExperimentDto createExperiment(CreateExperimentRequest createExperimentRequest, long createdUserId);

    ExperimentDto updateExperiment(UpdateExperimentRequest updateExperimentRequest);

    SampleDto createSample(CreateSampleRequest createSampleRequest);

    SampleDto updateSample(UpdateSampleRequest updateSampleRequest, long experimentId);
}
