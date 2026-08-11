package com.tuning.tuningprototype.models.db;

import java.math.BigDecimal;

// Dto record for creating and manipulating asset sales.
public record AssetSaleDto(
        Long id,
        Long saleDecisionId,
        Long purchaseLotId,
        String ticker,
        BigDecimal salePrice,
        BigDecimal saleAmount,
        Long saleTime,
        Long createdTime,
        Long modifiedTime) {}