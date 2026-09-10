package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Decision;
import com.tuning.tuningprototype.models.db.entity.DecisionDto;
import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.testutil.UninitializedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SampleMapperTest {

    @Mock
    private DecisionMapper decisionMapper;

    private SampleMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SampleMapper(decisionMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andDecisions_whenInitialized() {
        Decision decision = Decision.builder().id(5L).build();
        DecisionDto decisionDto = new DecisionDto(5L, 1L, null, null, null, null, null, null, null, null);
        when(decisionMapper.toDto(decision)).thenReturn(decisionDto);

        List<Decision> decisions = new ArrayList<>();
        decisions.add(decision);

        Sample entity = Sample.builder()
                .id(1L)
                .experimentId(2L)
                .marketInsights("insights")
                .samplingTime(1000L)
                .samplingStatus(SamplingStatus.COMPLETED)
                .createdTime(2000L)
                .modifiedTime(3000L)
                .decisions(decisions)
                .build();

        SampleDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.experimentId()).isEqualTo(2L);
        assertThat(dto.marketInsights()).isEqualTo("insights");
        assertThat(dto.samplingTime()).isEqualTo(1000L);
        assertThat(dto.samplingStatus()).isEqualTo(SamplingStatus.COMPLETED);
        assertThat(dto.createdTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
        assertThat(dto.decisions()).containsExactly(decisionDto);
    }

    @Test
    void toDto_decisionsIsNull_whenCollectionUninitialized() {
        Sample entity = Sample.builder()
                .id(1L)
                .decisions(new UninitializedList<>())
                .build();

        SampleDto dto = mapper.toDto(entity);

        assertThat(dto.decisions()).isNull();
        verifyNoInteractions(decisionMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        SampleDto dto = new SampleDto(
                1L, 2L, "insights", 1000L, SamplingStatus.DECIDING, 2000L, 3000L, List.of());

        Sample entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getExperimentId()).isEqualTo(2L);
        assertThat(entity.getMarketInsights()).isEqualTo("insights");
        assertThat(entity.getSamplingTime()).isEqualTo(1000L);
        assertThat(entity.getSamplingStatus()).isEqualTo(SamplingStatus.DECIDING);
        assertThat(entity.getCreatedTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
