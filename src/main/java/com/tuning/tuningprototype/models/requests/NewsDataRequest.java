package com.tuning.tuningprototype.models.requests;

import java.util.List;

// Request for news data about the provided tickers
public record NewsDataRequest(
        // Tickers for news to be returned for
        List<String> tickers,
        // Start time, defaults to current time if null
        Long startTime,
        // End time, defaults to start time + 15 minutes if null
        Long endTime) {}