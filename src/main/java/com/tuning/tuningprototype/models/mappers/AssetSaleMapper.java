package com.tuning.tuningprototype.models.mappers;

import com.tuning.tuningprototype.models.AssetSale;
import com.tuning.tuningprototype.models.AssetSaleDto;
import com.tuning.tuningprototype.models.Decision;
import com.tuning.tuningprototype.models.PurchaseLot;

public interface AssetSaleMapper {
    AssetSaleDto toDto(AssetSale assetSale);
    AssetSale toEntity(AssetSaleDto dto, Decision saleDecisionReference, PurchaseLot purchaseLotReference);
}