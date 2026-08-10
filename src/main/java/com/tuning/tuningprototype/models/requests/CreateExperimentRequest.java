package com.tuning.tuningprototype.models.requests;

import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.SamplingWindow;

import java.math.BigDecimal;

// Request for creating a new experiment
public record CreateExperimentRequest(
        String name,
        AgentModel agentModel,
        String strategyPrompt,
        SamplingWindow samplingWindow,
        Long experimentStartTime,
        Long experimentEndTime) {}