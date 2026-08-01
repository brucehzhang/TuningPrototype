package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.Experiment;
import com.tuning.tuningprototype.models.Wallet;
import com.tuning.tuningprototype.models.WalletDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletMapperImpl implements WalletMapper {

    @Lazy
    private final ExperimentMapper experimentMapper;
    @Lazy
    private final PurchaseLotMapper purchaseLotMapper;

    @Override
    public WalletDto toDto(Wallet wallet) {
        if (wallet == null) return null;

        Experiment experiment = wallet.getExperiment();

        return new WalletDto(
                wallet.getId(),
                experiment != null ? experiment.getId() : null,
                Hibernate.isInitialized(experiment) ? experimentMapper.toDto(experiment) : null,
                wallet.getCurrentMoneyAmount(),
                wallet.getCreatedTime(),
                wallet.getModifiedTime(),
                Hibernate.isInitialized(wallet.getPurchaseLots())
                        ? wallet.getPurchaseLots().stream().map(purchaseLotMapper::toDto).toList() : null
        );
    }

    @Override
    public Wallet toEntity(WalletDto dto, Experiment experimentReference) {
        if (dto == null) return null;

        return Wallet.builder()
                .id(dto.id())
                .experiment(experimentReference)
                .currentMoneyAmount(dto.currentMoneyAmount())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}