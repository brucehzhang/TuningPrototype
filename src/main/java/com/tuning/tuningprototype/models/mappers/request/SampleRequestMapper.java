package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.Sample;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;

// Mapper interface for samples requests (create, update, etc)
public interface SampleRequestMapper {
    Sample toEntity(CreateSampleRequest request, Long nowEpochSeconds);
    void applyUpdate(UpdateSampleRequest request, Sample existing, Long nowEpochSeconds);
}
