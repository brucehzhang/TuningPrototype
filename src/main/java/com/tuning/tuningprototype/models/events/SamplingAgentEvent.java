package com.tuning.tuningprototype.models.events;

import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.SamplingWindow;

public record SamplingAgentEvent(
        Long experimentId,
        String strategyPrompt,
        AgentModel agentModel,
        SamplingWindow samplingWindow,
        Long sampleId,
        Long samplingTime,
        ExperimentFinances experimentFinances) {
}
