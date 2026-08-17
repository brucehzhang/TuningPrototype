package com.tuning.tuningprototype.models.requests;

import com.tuning.tuningprototype.models.enums.DecisionType;

// Request dto for the decision itself. sampleId ties it to the sample that
// triggered this analysis; createdTime/modifiedTime are server-controlled.
public record CreateDecisionRequest(
        Long sampleId,
        DecisionType decisionType,
        String ticker,
        String reasoning,
        Long decisionTime) {}