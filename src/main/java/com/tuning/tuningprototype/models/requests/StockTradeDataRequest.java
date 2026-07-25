package com.tuning.tuningprototype.models.requests;

import java.util.List;

// Request for stock trade data, such as how many shares were bought at which exchange at what time.
public record StockTradeDataRequest(
        // Tickers to be returned
        List<String> tickers,
        // Start time, defaults to current time if null
        Long startTime,
        // End time, defaults to start time + 15 minutes if null
        Long endTime) {
}
