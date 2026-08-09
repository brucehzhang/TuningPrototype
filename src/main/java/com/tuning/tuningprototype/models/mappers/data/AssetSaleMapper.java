package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.AssetSale;
import com.tuning.tuningprototype.models.db.AssetSaleDto;

public interface AssetSaleMapper {
    AssetSaleDto toDto(AssetSale assetSale);
    AssetSale toEntity(AssetSaleDto dto);
}