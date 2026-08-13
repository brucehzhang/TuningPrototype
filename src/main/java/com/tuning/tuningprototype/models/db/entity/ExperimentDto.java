package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;

import java.util.List;

// Dto record for creating and manipulating experiments.
public record ExperimentDto(
        Long id,
        String name,
        AgentModel agentModel,
        String strategyPrompt,
        SamplingWindow samplingWindow,
        Long experimentStartTime,
        Long experimentEndTime,
        ExperimentStatus experimentStatus,
        Long createdTime,
        Long createdUserId,
        Long modifiedTime,
        List<SampleDto> samples,
        List<WalletDto> wallets) {}