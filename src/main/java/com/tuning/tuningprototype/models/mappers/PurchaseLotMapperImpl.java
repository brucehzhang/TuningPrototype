package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.db.PurchaseLot;
import com.tuning.tuningprototype.models.db.PurchaseLotDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PurchaseLotMapperImpl implements PurchaseLotMapper {

    private final AssetSaleMapper _assetSaleMapper;

    public PurchaseLotMapperImpl(@Lazy AssetSaleMapper assetSaleMapper) {
        _assetSaleMapper = assetSaleMapper;
    }

    @Override
    public PurchaseLotDto toDto(PurchaseLot purchaseLot) {
        if (purchaseLot == null) return null;

        return new PurchaseLotDto(
                purchaseLot.getId(),
                purchaseLot.getPurchaseDecisionId(),
                purchaseLot.getWalletId(),
                purchaseLot.getTicker(),
                purchaseLot.getPurchasePrice(),
                purchaseLot.getPurchaseAmount(),
                purchaseLot.getCreatedTime(),
                purchaseLot.getModifiedTime(),
                Hibernate.isInitialized(purchaseLot.getAssetSales())
                        ? purchaseLot.getAssetSales().stream().map(_assetSaleMapper::toDto).toList() : null
        );
    }

    @Override
    public PurchaseLot toEntity(PurchaseLotDto dto) {
        if (dto == null) return null;

        return PurchaseLot.builder()
                .id(dto.id())
                .purchaseDecisionId(dto.purchaseDecisionId())
                .walletId(dto.walletId())
                .ticker(dto.ticker())
                .purchasePrice(dto.purchasePrice())
                .purchaseAmount(dto.purchaseAmount())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}