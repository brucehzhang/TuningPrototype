package com.tuning.tuningprototype.models.requests;

import java.util.List;

// Request for aggregate data (bars) in blocks of the requested timeframe from start to end time.
public record StockAggregateDataRequest(
        List<String> tickers,
        String timeFrame,
        Long startTime,
        Long endTime) {
}

