package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Decision;
import com.tuning.tuningprototype.models.db.entity.DecisionDto;

public interface DecisionMapper {
    DecisionDto toDto(Decision decision);
    Decision toEntity(DecisionDto dto);
}