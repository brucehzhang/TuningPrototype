package com.tuning.tuningprototype.models.requests;

// Request dto for creating a new sample under an experiment.
// samplingStatus/createdTime/modifiedTime are server-controlled — a newly created
// sample always starts as IN_PROGRESS until the sampling pipeline completes it.
public record CreateSampleRequest(
        Long experimentId,
        String marketInsights,
        Long samplingTime) {}