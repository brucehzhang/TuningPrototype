package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;

public interface ExperimentMapper {
    ExperimentDto toDto(Experiment experiment);
    Experiment toEntity(ExperimentDto dto);
}