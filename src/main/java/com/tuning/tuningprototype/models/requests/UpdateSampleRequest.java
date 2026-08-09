package com.tuning.tuningprototype.models.requests;

import com.tuning.tuningprototype.models.enums.SamplingStatus;

// Request dto for updating an existing sample. All fields nullable/optional —
// PATCH semantics, only non-null fields are applied. experimentId is intentionally
// excluded — a sample shouldn't be re-parented to a different experiment after creation.
public record UpdateSampleRequest(
        Long id,
        String marketInsights,
        SamplingStatus samplingStatus) {}