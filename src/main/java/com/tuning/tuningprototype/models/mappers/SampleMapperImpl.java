package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Experiment;
import com.tuning.tuningprototype.models.Sample;
import com.tuning.tuningprototype.models.SampleDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SampleMapperImpl implements SampleMapper {

    @Lazy
    private final ExperimentMapper experimentMapper;
    @Lazy
    private final DecisionMapper decisionMapper;

    @Override
    public SampleDto toDto(Sample sample) {
        if (sample == null) return null;

        Experiment experiment = sample.getExperiment();

        return new SampleDto(
                sample.getId(),
                experiment != null ? experiment.getId() : null,
                Hibernate.isInitialized(experiment) ? experimentMapper.toDto(experiment) : null,
                sample.getMarketInsights(),
                sample.getSamplingTime(),
                sample.getSamplingStatus(),
                sample.getCreatedTime(),
                sample.getModifiedTime(),
                Hibernate.isInitialized(sample.getDecisions())
                        ? sample.getDecisions().stream().map(decisionMapper::toDto).toList() : null
        );
    }

    @Override
    public Sample toEntity(SampleDto dto, Experiment experimentReference) {
        if (dto == null) return null;

        return Sample.builder()
                .id(dto.id())
                .experiment(experimentReference)
                .marketInsights(dto.marketInsights())
                .samplingTime(dto.samplingTime())
                .samplingStatus(dto.samplingStatus())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}