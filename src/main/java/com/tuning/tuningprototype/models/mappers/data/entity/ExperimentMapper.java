package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;

public interface ExperimentMapper {
    ExperimentDto toDto(Experiment experiment);
    Experiment toEntity(ExperimentDto dto);
}