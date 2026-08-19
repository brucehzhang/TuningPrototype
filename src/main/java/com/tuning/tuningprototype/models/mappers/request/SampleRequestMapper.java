package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import org.springframework.stereotype.Component;

@Component
public class SampleRequestMapper {

    /**
     * Builds a new Sample from a create request. samplingStatus always starts as
     * IN_PROGRESS — clients can't set the initial status directly; it's driven by
     * the sampling pipeline via subsequent updates.
     */
    public Sample toEntity(CreateSampleRequest request, Long nowEpochSeconds) {
        return Sample.builder()
                .experimentId(request.experimentId())
                .marketInsights(request.marketInsights())
                .samplingTime(request.samplingTime())
                .samplingStatus(SamplingStatus.IN_PROGRESS) // server-controlled
                .createdTime(nowEpochSeconds)                // server-controlled
                .modifiedTime(nowEpochSeconds)                // server-controlled
                .build();
    }

    /**
     * Applies a partial update onto an already-loaded, managed Sample entity.
     * Mutates in place rather than constructing a new entity so fields the client
     * didn't touch (e.g. experimentId, samplingTime) are preserved untouched.
     */
    public void applyUpdate(UpdateSampleRequest request, Sample existing, Long nowEpochSeconds) {
        if (request.marketInsights() != null) {
            existing.setMarketInsights(request.marketInsights());
        }
        if (request.samplingStatus() != null) {
            existing.setSamplingStatus(request.samplingStatus());
        }
        existing.setModifiedTime(nowEpochSeconds);
    }
}