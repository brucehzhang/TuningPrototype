package com.tuning.tuningprototype.models;

import com.tuning.tuningprototype.models.enums.DecisionType;
import java.util.List;

// Dto record for creating and manipulating decisions.
// `sampleId` is always available; `sample` is null unless explicitly fetched.
public record DecisionDto(
        Long id,
        Long sampleId,
        SampleDto sample,
        DecisionType decisionType,
        String ticker,
        String reasoning,
        Long decisionTime,
        Long createdTime,
        Long modifiedTime,
        List<PurchaseLotDto> purchaseLots,
        List<AssetSaleDto> assetSales) {}