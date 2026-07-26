package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.AgentModels;

// Dto record for creating and manipulating experiments
public record ExperimentDto(
        Long id,
        String name,
        AgentModels model,
        String strategyPrompt,
        Long experimentStartTime,
        Long experimentEndTime,
        Long createdTime,
        Long modifiedTime) {}