package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Sample;
import com.tuning.tuningprototype.models.db.SampleDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SampleMapperImpl implements SampleMapper {

    private final DecisionMapper _decisionMapper;

    public SampleMapperImpl(@Lazy DecisionMapper decisionMapper) {
        _decisionMapper = decisionMapper;
    }

    @Override
    public SampleDto toDto(Sample sample) {
        if (sample == null) return null;

        return new SampleDto(
                sample.getId(),
                sample.getExperimentId(),
                sample.getMarketInsights(),
                sample.getSamplingTime(),
                sample.getSamplingStatus(),
                sample.getCreatedTime(),
                sample.getModifiedTime(),
                Hibernate.isInitialized(sample.getDecisions())
                        ? sample.getDecisions().stream().map(_decisionMapper::toDto).toList() : null
        );
    }

    @Override
    public Sample toEntity(SampleDto dto) {
        if (dto == null) return null;

        return Sample.builder()
                .id(dto.id())
                .experimentId(dto.experimentId())
                .marketInsights(dto.marketInsights())
                .samplingTime(dto.samplingTime())
                .samplingStatus(dto.samplingStatus())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}