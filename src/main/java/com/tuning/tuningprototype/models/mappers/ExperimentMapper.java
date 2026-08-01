package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Experiment;
import com.tuning.tuningprototype.models.ExperimentDto;
import com.tuning.tuningprototype.models.User;

public interface ExperimentMapper {
    ExperimentDto toDto(Experiment experiment);
    Experiment toEntity(ExperimentDto dto, User createdByUserReference);
}