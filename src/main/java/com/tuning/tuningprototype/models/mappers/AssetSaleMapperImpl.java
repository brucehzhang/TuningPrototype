package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.AssetSale;
import com.tuning.tuningprototype.models.AssetSaleDto;
import com.tuning.tuningprototype.models.Decision;
import com.tuning.tuningprototype.models.PurchaseLot;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetSaleMapperImpl implements AssetSaleMapper {

    @Lazy
    private final DecisionMapper decisionMapper;
    @Lazy
    private final PurchaseLotMapper purchaseLotMapper;

    @Override
    public AssetSaleDto toDto(AssetSale assetSale) {
        if (assetSale == null) return null;

        Decision saleDecision = assetSale.getSaleDecision();
        PurchaseLot purchaseLot = assetSale.getPurchaseLot();

        return new AssetSaleDto(
                assetSale.getId(),
                saleDecision != null ? saleDecision.getId() : null,
                Hibernate.isInitialized(saleDecision) ? decisionMapper.toDto(saleDecision) : null,
                purchaseLot != null ? purchaseLot.getId() : null,
                Hibernate.isInitialized(purchaseLot) ? purchaseLotMapper.toDto(purchaseLot) : null,
                assetSale.getTicker(),
                assetSale.getSalePrice(),
                assetSale.getSaleAmount(),
                assetSale.getCreatedTime(),
                assetSale.getModifiedTime()
        );
    }

    @Override
    public AssetSale toEntity(AssetSaleDto dto, Decision saleDecisionReference, PurchaseLot purchaseLotReference) {
        if (dto == null) return null;

        return AssetSale.builder()
                .id(dto.id())
                .saleDecision(saleDecisionReference)
                .purchaseLot(purchaseLotReference)
                .ticker(dto.ticker())
                .salePrice(dto.salePrice())
                .saleAmount(dto.saleAmount())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}