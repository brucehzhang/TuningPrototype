package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.Decision;
import com.tuning.tuningprototype.models.db.DecisionDto;

public interface DecisionMapper {
    DecisionDto toDto(Decision decision);
    Decision toEntity(DecisionDto dto);
}