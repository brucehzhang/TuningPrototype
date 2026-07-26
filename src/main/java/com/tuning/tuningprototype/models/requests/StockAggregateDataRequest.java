package com.tuning.tuningprototype.models.requests;

import java.util.List;

// Request for aggregate data (bars) in blocks of the requested timeframe from start to end time.
public record StockAggregateDataRequest(
        // Tickers to be returned
        List<String> tickers,
        // Timeframe used to split up individual aggregate groupings. Available groupings: ([1-59]Min, [1-24]Hour, 1Day, 1Week, [1,2,3,4,6,12]Month)
        String timeframe,
        // Start time in unix seconds, defaults to current time if null
        Long startTime,
        // End time in unix seconds, defaults to start time + 15 minutes if null
        Long endTime) {}