package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ExperimentMapperImpl implements ExperimentMapper {

    private final SampleMapper _sampleMapper;
    private final WalletMapper _walletMapper;

    public ExperimentMapperImpl(@Lazy SampleMapper sampleMapper, @Lazy WalletMapper walletMapper) {
        _sampleMapper = sampleMapper;
        _walletMapper = walletMapper;
    }

    @Override
    public ExperimentDto toDto(Experiment experiment) {
        if (experiment == null) return null;

        return new ExperimentDto(
                experiment.getId(),
                experiment.getName(),
                experiment.getAgentModel(),
                experiment.getStrategyPrompt(),
                experiment.getSamplingWindow(),
                experiment.getExperimentStartTime(),
                experiment.getExperimentEndTime(),
                experiment.getExperimentStatus(),
                experiment.getCreatedTime(),
                experiment.getCreatedUserId(),
                experiment.getModifiedTime(),
                Hibernate.isInitialized(experiment.getSamples())
                        ? experiment.getSamples().stream().map(_sampleMapper::toDto).toList() : null,
                Hibernate.isInitialized(experiment.getWallets())
                        ? experiment.getWallets().stream().map(_walletMapper::toDto).toList() : null
        );
    }

    @Override
    public Experiment toEntity(ExperimentDto dto) {
        if (dto == null) return null;

        return Experiment.builder()
                .id(dto.id())
                .name(dto.name())
                .agentModel(dto.agentModel())
                .strategyPrompt(dto.strategyPrompt())
                .samplingWindow(dto.samplingWindow())
                .experimentStartTime(dto.experimentStartTime())
                .experimentEndTime(dto.experimentEndTime())
                .experimentStatus(dto.experimentStatus())
                .createdTime(dto.createdTime())
                .createdUserId(dto.createdUserId())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}