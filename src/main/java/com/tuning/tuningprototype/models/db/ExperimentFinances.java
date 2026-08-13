package com.tuning.tuningprototype.models.db;

import java.math.BigDecimal;
import java.util.List;

// Snapshot record for an experiment's point in time financials
public record ExperimentFinances(
        Long experimentId,
        Long asOf,
        List<ActiveWalletBalance> activeWalletBalances
) {

    // Snapshot record for wallet's current balance (active lots + sold lots) and active purchase lots at point in time
    public record ActiveWalletBalance(
            Long walletId,
            BigDecimal startingMoneyAmount,
            BigDecimal currentMoneyAmount,
            String currencyCode,
            List<ActivePurchaseLot> activePurchaseLots
    ) {

        // Snapshot record for active purchase lots with the current quantity after sales at point in time
        public record ActivePurchaseLot(
                Long purchaseLotId,
                String ticker,
                BigDecimal purchasePrice,
                BigDecimal currentQuantity
        ) {

        }
    }
}


