package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Decision;
import com.tuning.tuningprototype.models.db.DecisionDto;

public interface DecisionMapper {
    DecisionDto toDto(Decision decision);
    Decision toEntity(DecisionDto dto);
}