package com.tuning.tuningprototype.models;

import java.math.BigDecimal;
import java.util.List;

// Dto record for creating and manipulating wallets.
// `experimentId` is always available; `experiment` is null unless explicitly fetched.
public record WalletDto(
        Long id,
        Long experimentId,
        ExperimentDto experiment,
        BigDecimal currentMoneyAmount,
        Long createdTime,
        Long modifiedTime,
        List<PurchaseLotDto> purchaseLots) {}