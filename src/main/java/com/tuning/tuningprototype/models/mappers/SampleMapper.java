package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Sample;
import com.tuning.tuningprototype.models.db.SampleDto;

public interface SampleMapper {
    SampleDto toDto(Sample sample);
    Sample toEntity(SampleDto dto);
}