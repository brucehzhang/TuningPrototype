package com.tuning.tuningprototype.models.db;

import java.math.BigDecimal;
import java.util.List;

// Dto record for creating and manipulating purchase lots.
public record PurchaseLotDto(
        Long id,
        Long purchaseDecisionId,
        Long walletId,
        String ticker,
        BigDecimal purchasePrice,
        BigDecimal purchaseAmount,
        Long purchaseTime,
        Long createdTime,
        Long modifiedTime,
        List<AssetSaleDto> assetSales) {}