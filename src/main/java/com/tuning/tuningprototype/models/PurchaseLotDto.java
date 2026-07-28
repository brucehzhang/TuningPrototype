package com.tuning.tuningprototype.models;

import java.math.BigDecimal;
import java.util.List;

// Dto record for creating and manipulating purchase lots.
// `purchaseDecisionId`/`walletId` are always available; nested objects are null unless explicitly fetched.
public record PurchaseLotDto(
        Long id,
        Long purchaseDecisionId,
        DecisionDto purchaseDecision,
        Long walletId,
        WalletDto wallet,
        String ticker,
        BigDecimal purchasePrice,
        BigDecimal purchaseAmount,
        Long createdTime,
        Long modifiedTime,
        List<AssetSaleDto> assetSales) {}