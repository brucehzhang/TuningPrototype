package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.db.entity.SampleDto;

public interface SampleMapper {
    SampleDto toDto(Sample sample);
    Sample toEntity(SampleDto dto);
}