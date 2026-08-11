package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.AssetSale;
import com.tuning.tuningprototype.models.db.AssetSaleDto;
import org.springframework.stereotype.Component;

@Component
public class AssetSaleMapperImpl implements AssetSaleMapper {

    @Override
    public AssetSaleDto toDto(AssetSale assetSale) {
        if (assetSale == null) return null;

        return new AssetSaleDto(
                assetSale.getId(),
                assetSale.getSaleDecisionId(),
                assetSale.getPurchaseLotId(),
                assetSale.getTicker(),
                assetSale.getSalePrice(),
                assetSale.getSaleAmount(),
                assetSale.getSaleTime(),
                assetSale.getCreatedTime(),
                assetSale.getModifiedTime()
        );
    }

    @Override
    public AssetSale toEntity(AssetSaleDto dto) {
        if (dto == null) return null;

        return AssetSale.builder()
                .id(dto.id())
                .saleDecisionId(dto.saleDecisionId())
                .purchaseLotId(dto.purchaseLotId())
                .ticker(dto.ticker())
                .salePrice(dto.salePrice())
                .saleAmount(dto.saleAmount())
                .saleTime(dto.saleTime())
                .createdTime(dto.createdTime())
                .modifiedTime(dto.modifiedTime())
                .build();
    }
}