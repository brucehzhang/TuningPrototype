package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Experiment;
import com.tuning.tuningprototype.models.Sample;
import com.tuning.tuningprototype.models.SampleDto;

public interface SampleMapper {
    SampleDto toDto(Sample sample);
    Sample toEntity(SampleDto dto, Experiment experimentReference);
}