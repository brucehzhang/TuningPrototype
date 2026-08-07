package com.tuning.tuningprototype.models.requests;

import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;

import java.math.BigDecimal;

public record UpdateExperimentRequest(String name,
                                      AgentModel agentModel,
                                      String strategyPrompt,
                                      SamplingWindow samplingWindow,
                                      BigDecimal startingMoneyAmount,
                                      String currencyCode,
                                      Long experimentStartTime,
                                      Long experimentEndTime,
                                      ExperimentStatus experimentStatus) {
}
