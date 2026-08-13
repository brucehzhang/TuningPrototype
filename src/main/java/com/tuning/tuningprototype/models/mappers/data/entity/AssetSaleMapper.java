package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.AssetSale;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;

public interface AssetSaleMapper {
    AssetSaleDto toDto(AssetSale assetSale);
    AssetSale toEntity(AssetSaleDto dto);
}