package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.ExperimentDto;

import java.util.Optional;

public interface IExperimentService {

    Optional<ExperimentDto> getExperiment(long id);
}
