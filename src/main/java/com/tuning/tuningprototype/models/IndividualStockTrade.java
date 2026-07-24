package com.tuning.tuningprototype.models;

import java.math.BigInteger;

// Individual stock trade
public record IndividualStockTrade(
        Integer tradeId,
        Double price,
        Long tradeSize,
        String dateTime,
        String exchange) {
}