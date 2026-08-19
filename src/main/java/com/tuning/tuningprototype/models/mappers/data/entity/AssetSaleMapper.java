package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;
import org.springframework.stereotype.Component;

@Component
public class AssetSaleMapper{

    public AssetSaleDto toDto(AssetSale assetSale) {
        if (assetSale == null) return null;

        return new AssetSaleDto(
                assetSale.getId(),
                assetSale.getSaleDecisionId(),
                assetSale.getPurchaseLotId(),
                assetSale.getTicker(),
                assetSale.getSalePrice(),
                assetSale.getSaleQuantity(),
                assetSale.getSaleTime(),
                assetSale.getCreatedTime(),
                assetSale.getModifiedTime()
        );
    }

    public AssetSale toEntity(AssetSaleDto dto) {
        if (dto == null) return null;

        return AssetSale.builder()
                .id(dto.id())
                .saleDecisionId(dto.saleDecisionId())
                .purchaseLotId(dto.purchaseLotId())
                .ticker(dto.ticker())
                .salePrice(dto.salePrice())
                .saleQuantity(dto.saleQuantity())
                .saleTime(dto.saleTime())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}