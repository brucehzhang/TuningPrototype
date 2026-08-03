package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.Wallet;
import com.tuning.tuningprototype.models.db.WalletDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class WalletMapperImpl implements WalletMapper {

    private final PurchaseLotMapper _purchaseLotMapper;

    public WalletMapperImpl(@Lazy PurchaseLotMapper purchaseLotMapper) {
        _purchaseLotMapper = purchaseLotMapper;
    }

    @Override
    public WalletDto toDto(Wallet wallet) {
        if (wallet == null) return null;

        return new WalletDto(
                wallet.getId(),
                wallet.getExperimentId(),
                wallet.getCurrentMoneyAmount(),
                wallet.getCreatedTime(),
                wallet.getModifiedTime(),
                Hibernate.isInitialized(wallet.getPurchaseLots())
                        ? wallet.getPurchaseLots().stream().map(_purchaseLotMapper::toDto).toList() : null
        );
    }

    @Override
    public Wallet toEntity(WalletDto dto) {
        if (dto == null) return null;

        return Wallet.builder()
                .id(dto.id())
                .experimentId(dto.experimentId())
                .currentMoneyAmount(dto.currentMoneyAmount())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}