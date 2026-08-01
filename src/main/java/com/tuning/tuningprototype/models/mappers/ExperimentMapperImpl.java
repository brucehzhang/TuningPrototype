package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Experiment;
import com.tuning.tuningprototype.models.ExperimentDto;
import com.tuning.tuningprototype.models.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExperimentMapperImpl implements ExperimentMapper {

    @Lazy
    private final UserMapper userMapper;
    @Lazy
    private final SampleMapper sampleMapper;
    @Lazy
    private final WalletMapper walletMapper;

    @Override
    public ExperimentDto toDto(Experiment experiment) {
        if (experiment == null) return null;

        User createdByUser = experiment.getCreatedByUser();

        return new ExperimentDto(
                experiment.getId(),
                experiment.getName(),
                experiment.getAgentModel(),
                experiment.getStrategyPrompt(),
                experiment.getSamplingWindow(),
                experiment.getStartingMoneyAmount(),
                experiment.getCurrencyCode(),
                experiment.getExperimentStartTime(),
                experiment.getExperimentEndTime(),
                experiment.getExperimentStatus(),
                experiment.getCreatedTime(),
                createdByUser != null ? createdByUser.getId() : null,
                Hibernate.isInitialized(createdByUser) ? userMapper.toDto(createdByUser) : null,
                experiment.getModifiedTime(),
                Hibernate.isInitialized(experiment.getSamples())
                        ? experiment.getSamples().stream().map(sampleMapper::toDto).toList() : null,
                Hibernate.isInitialized(experiment.getWallets())
                        ? experiment.getWallets().stream().map(walletMapper::toDto).toList() : null
        );
    }

    @Override
    public Experiment toEntity(ExperimentDto dto, User createdByUserReference) {
        if (dto == null) return null;

        return Experiment.builder()
                .id(dto.id())
                .name(dto.name())
                .agentModel(dto.agentModel())
                .strategyPrompt(dto.strategyPrompt())
                .samplingWindow(dto.samplingWindow())
                .startingMoneyAmount(dto.startingMoneyAmount())
                .currencyCode(dto.currencyCode())
                .experimentStartTime(dto.experimentStartTime())
                .experimentEndTime(dto.experimentEndTime())
                .experimentStatus(dto.experimentStatus())
                .createdTime(dto.createdTime())
                .createdByUser(createdByUserReference)
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}