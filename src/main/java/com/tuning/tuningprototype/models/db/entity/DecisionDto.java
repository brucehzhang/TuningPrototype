package com.tuning.tuningprototype.models.db.entity;

import com.tuning.tuningprototype.models.enums.DecisionType;
import java.util.List;

// Dto record for creating and manipulating decisions.
public record DecisionDto(
        Long id,
        Long sampleId,
        DecisionType decisionType,
        String ticker,
        String reasoning,
        Long decisionTime,
        Long createdTime,
        Long modifiedTime,
        List<PurchaseLotDto> purchaseLots,
        List<AssetSaleDto> assetSales) {}