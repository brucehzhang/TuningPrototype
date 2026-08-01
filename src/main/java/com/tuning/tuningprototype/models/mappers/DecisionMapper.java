package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Decision;
import com.tuning.tuningprototype.models.DecisionDto;
import com.tuning.tuningprototype.models.Sample;

public interface DecisionMapper {
    DecisionDto toDto(Decision decision);
    Decision toEntity(DecisionDto dto, Sample sampleReference);
}