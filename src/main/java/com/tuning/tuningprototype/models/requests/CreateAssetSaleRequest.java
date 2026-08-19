package com.tuning.tuningprototype.models.requests;

import java.math.BigDecimal;

// Request dto for an asset sale resulting from a SELL decision.
// saleDecisionId is intentionally excluded — set by the service from the Decision
// created in the same call. purchaseLotId IS client-supplied, since the agent (or
// upstream lot-matching logic) determines which existing lot(s) a sale closes out.
public record CreateAssetSaleRequest(
        Long purchaseLotId,
        String ticker,
        BigDecimal salePrice,
        BigDecimal saleQuantity,
        Long saleTime) {}