package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.SamplingStatus;
import java.util.List;

// Dto record for creating and manipulating samples.
// `experimentId` is always available; `experiment` is null unless explicitly fetched.
public record SampleDto(
        Long id,
        Long experimentId,
        ExperimentDto experiment,
        String marketInsights,
        Long samplingTime,
        SamplingStatus samplingStatus,
        Long createdTime,
        Long modifiedTime,
        List<DecisionDto> decisions) {}