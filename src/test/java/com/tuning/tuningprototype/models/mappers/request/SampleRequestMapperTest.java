package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SampleRequestMapperTest {

    private final SampleRequestMapper mapper = new SampleRequestMapper();

    @Test
    void toEntity_mapsRequestFields_andSetsServerControlledFields() {
        CreateSampleRequest request = new CreateSampleRequest(1L, "insights", 1000L);

        Sample entity = mapper.toEntity(request, 5000L);

        assertThat(entity.getExperimentId()).isEqualTo(1L);
        assertThat(entity.getMarketInsights()).isEqualTo("insights");
        assertThat(entity.getSamplingTime()).isEqualTo(1000L);
        assertThat(entity.getSamplingStatus()).isEqualTo(SamplingStatus.IN_PROGRESS);
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }

    private Sample existingSample() {
        return Sample.builder()
                .id(1L)
                .experimentId(2L)
                .marketInsights("original insights")
                .samplingTime(1000L)
                .samplingStatus(SamplingStatus.IN_PROGRESS)
                .createdTime(2000L)
                .modifiedTime(3000L)
                .build();
    }

    @Test
    void applyUpdate_overwritesFields_whenRequestFieldsAreNonNull() {
        Sample existing = existingSample();
        UpdateSampleRequest request = new UpdateSampleRequest(1L, "updated insights", SamplingStatus.COMPLETED);

        mapper.applyUpdate(request, existing, 9000L);

        assertThat(existing.getMarketInsights()).isEqualTo("updated insights");
        assertThat(existing.getSamplingStatus()).isEqualTo(SamplingStatus.COMPLETED);
        assertThat(existing.getModifiedTime()).isEqualTo(9000L);
        // untouched fields remain
        assertThat(existing.getExperimentId()).isEqualTo(2L);
        assertThat(existing.getSamplingTime()).isEqualTo(1000L);
    }

    @Test
    void applyUpdate_leavesExistingValues_whenRequestFieldsAreNull() {
        Sample existing = existingSample();
        UpdateSampleRequest request = new UpdateSampleRequest(1L, null, null);

        mapper.applyUpdate(request, existing, 9000L);

        assertThat(existing.getMarketInsights()).isEqualTo("original insights");
        assertThat(existing.getSamplingStatus()).isEqualTo(SamplingStatus.IN_PROGRESS);
        assertThat(existing.getModifiedTime()).isEqualTo(9000L);
    }
}
