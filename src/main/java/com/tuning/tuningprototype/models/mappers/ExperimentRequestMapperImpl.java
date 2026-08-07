package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import org.springframework.stereotype.Component;

@Component
public class ExperimentRequestMapperImpl implements ExperimentRequestMapper {

    @Override
    public Experiment toEntity(CreateExperimentRequest request, Long createdUserId, Long nowEpochSeconds) {
        return Experiment.builder()
                .name(request.name())
                .agentModel(request.agentModel())
                .strategyPrompt(request.strategyPrompt())
                .samplingWindow(request.samplingWindow())
                .startingMoneyAmount(request.startingMoneyAmount())
                .currencyCode(request.currencyCode())
                .experimentStartTime(request.experimentStartTime())
                .experimentEndTime(request.experimentEndTime())
                .experimentStatus(ExperimentStatus.DRAFT)      // server-controlled default
                .createdUserId(createdUserId)                   // server-controlled, from auth
                .createdTime(nowEpochSeconds)                   // server-controlled
                .modifiedTime(nowEpochSeconds)                  // server-controlled
                .build();
    }

    @Override
    public void applyUpdate(UpdateExperimentRequest request, Experiment existing, Long nowEpochSeconds) {
        if (request.name() != null) existing.setName(request.name());
        if (request.agentModel() != null) existing.setAgentModel(request.agentModel());
        if (request.strategyPrompt() != null) existing.setStrategyPrompt(request.strategyPrompt());
        if (request.samplingWindow() != null) existing.setSamplingWindow(request.samplingWindow());
        if (request.startingMoneyAmount() != null) existing.setStartingMoneyAmount(request.startingMoneyAmount());
        if (request.currencyCode() != null) existing.setCurrencyCode(request.currencyCode());
        if (request.experimentStartTime() != null) existing.setExperimentStartTime(request.experimentStartTime());
        if (request.experimentEndTime() != null) existing.setExperimentEndTime(request.experimentEndTime());
        existing.setModifiedTime(nowEpochSeconds);
    }
}