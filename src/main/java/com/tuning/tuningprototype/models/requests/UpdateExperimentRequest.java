package com.tuning.tuningprototype.models.requests;

import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.SamplingWindow;

import java.math.BigDecimal;

// Request for updating an experiment's details
public record UpdateExperimentRequest(
        Long id,
        String name,
        AgentModel agentModel,
        String strategyPrompt,
        SamplingWindow samplingWindow,
        BigDecimal startingMoneyAmount,
        String currencyCode,
        Long experimentStartTime,
        Long experimentEndTime) {}