package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;

import java.math.BigDecimal;

// Dto record for creating and manipulating experiments
public record ExperimentDto(
        Long id,
        String name,
        AgentModel agentModel,
        String strategyPrompt,
        SamplingWindow samplingWindow,
        BigDecimal startingMoneyAmount,
        String currencyCode,
        Long experimentStartTime,
        Long experimentEndTime,
        ExperimentStatus experimentStatus,
        Long createdTime,
        Long createdByUserId,
        Long modifiedTime) {}