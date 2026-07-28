package com.tuning.tuningprototype.models;

import java.math.BigDecimal;

// Dto record for creating and manipulating asset sales.
// `saleDecisionId`/`purchaseLotId` are always available; nested objects are null unless explicitly fetched.
public record AssetSaleDto(
        Long id,
        Long saleDecisionId,
        DecisionDto saleDecision,
        Long purchaseLotId,
        PurchaseLotDto purchaseLot,
        String ticker,
        BigDecimal salePrice,
        BigDecimal saleAmount,
        Long createdTime,
        Long modifiedTime) {}