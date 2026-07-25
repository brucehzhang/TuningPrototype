package com.tuning.tuningprototype.models;

import java.time.OffsetDateTime;

// Individual stock trade details in a more easily readable format
public record IndividualStockTrade(
        Integer tradeId,
        Double price,
        Integer tradeSize,
        OffsetDateTime dateTime,
        String exchangeGroup) {
}