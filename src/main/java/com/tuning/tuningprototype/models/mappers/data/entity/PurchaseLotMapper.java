package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PurchaseLotMapper {

    private final AssetSaleMapper _assetSaleMapper;

    public PurchaseLotMapper(@Lazy AssetSaleMapper assetSaleMapper) {
        _assetSaleMapper = assetSaleMapper;
    }

    public PurchaseLotDto toDto(PurchaseLot purchaseLot) {
        if (purchaseLot == null) return null;

        return new PurchaseLotDto(
                purchaseLot.getId(),
                purchaseLot.getPurchaseDecisionId(),
                purchaseLot.getWalletId(),
                purchaseLot.getTicker(),
                purchaseLot.getPurchasePrice(),
                purchaseLot.getPurchaseQuantity(),
                purchaseLot.getPurchaseTime(),
                purchaseLot.getCreatedTime(),
                purchaseLot.getModifiedTime(),
                Hibernate.isInitialized(purchaseLot.getAssetSales())
                        ? purchaseLot.getAssetSales().stream().map(_assetSaleMapper::toDto).toList() : null
        );
    }

    public PurchaseLot toEntity(PurchaseLotDto dto) {
        if (dto == null) return null;

        return PurchaseLot.builder()
                .id(dto.id())
                .purchaseDecisionId(dto.purchaseDecisionId())
                .walletId(dto.walletId())
                .ticker(dto.ticker())
                .purchasePrice(dto.purchasePrice())
                .purchaseQuantity(dto.purchaseQuantity())
                .purchaseTime(dto.purchaseTime())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}