package com.tuning.tuningprototype.models.db;

import java.math.BigDecimal;
import java.util.List;

// Dto record for creating and manipulating wallets.
public record WalletDto(
        Long id,
        Long experimentId,
        BigDecimal currentMoneyAmount,
        String currencyCode,
        Long createdTime,
        Long modifiedTime,
        List<PurchaseLotDto> purchaseLots) {}