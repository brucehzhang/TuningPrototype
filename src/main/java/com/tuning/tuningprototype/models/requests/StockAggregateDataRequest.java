package com.tuning.tuningprototype.models.requests;

import java.util.List;

// Request for aggregate data (bars) in blocks of the requested timeframe from start to end time.
public record StockAggregateDataRequest(
        // Tickers to be returned
        List<String> tickers,
        // Timeframe used to split up individual aggregate groupings
        String timeframe,
        // Start time, defaults to current time if null
        Long startTime,
        // End time, defaults to start time + 15 minutes if null
        Long endTime) {}