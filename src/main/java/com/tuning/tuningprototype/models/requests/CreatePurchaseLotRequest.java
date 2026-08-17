package com.tuning.tuningprototype.models.requests;

import java.math.BigDecimal;

// Request dto for a purchase lot resulting from a BUY decision. purchaseDecisionId is set by the service from the Decision created in the same call
public record CreatePurchaseLotRequest(
        Long walletId,
        String ticker,
        BigDecimal purchasePrice,
        BigDecimal purchaseQuantity,
        Long purchaseTime) {}