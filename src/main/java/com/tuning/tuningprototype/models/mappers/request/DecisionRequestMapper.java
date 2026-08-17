package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.requests.CreateDecisionRequest;
import com.tuning.tuningprototype.models.db.entity.Decision;
import org.springframework.stereotype.Component;

@Component
public class DecisionRequestMapper {

    public Decision toEntity(CreateDecisionRequest request, Long nowEpochSeconds) {
        return Decision.builder()
                .sampleId(request.sampleId())
                .decisionType(request.decisionType())
                .ticker(request.ticker())
                .reasoning(request.reasoning())
                .decisionTime(request.decisionTime())
                .createdTime(nowEpochSeconds)  // server-controlled
                .modifiedTime(nowEpochSeconds) // server-controlled
                .build();
    }
}