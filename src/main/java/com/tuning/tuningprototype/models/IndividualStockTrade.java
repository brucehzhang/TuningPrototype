package com.tuning.tuningprototype.models;

import java.time.OffsetDateTime;

// Individual stock trade details in a more easily readable format
public record IndividualStockTrade(
        Long tradeId,
        Double price,
        Long tradeSize,
        OffsetDateTime dateTime,
        String exchangeGroup) {}