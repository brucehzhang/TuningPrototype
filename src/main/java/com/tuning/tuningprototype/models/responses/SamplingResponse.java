package com.tuning.tuningprototype.models.responses;

import com.tuning.tuningprototype.models.db.entity.SampleDto;

public record SamplingResponse(
        // Sample that was updated
        SampleDto updatedSample,
        // sample that was created after the previous Sample, can be null.
        SampleDto nextCreatedSample) {
}
