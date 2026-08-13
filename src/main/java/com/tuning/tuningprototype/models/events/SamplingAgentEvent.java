package com.tuning.tuningprototype.models.events;

import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.enums.AgentModel;

public record SamplingAgentEvent(
        Long experimentId,
        String strategyPrompt,
        AgentModel agentModel,
        Long sampleId,
        Long samplingTime,
        ExperimentFinances experimentFinances) {
}
