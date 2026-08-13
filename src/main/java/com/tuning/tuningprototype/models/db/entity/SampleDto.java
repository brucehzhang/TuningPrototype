package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.enums.SamplingStatus;
import java.util.List;

// Dto record for creating and manipulating samples.
public record SampleDto(
        Long id,
        Long experimentId,
        String marketInsights,
        Long samplingTime,
        SamplingStatus samplingStatus,
        Long createdTime,
        Long modifiedTime,
        List<DecisionDto> decisions) {}