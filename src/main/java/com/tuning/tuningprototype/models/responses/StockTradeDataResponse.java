package com.tuning.tuningprototype.models.responses;

import com.tuning.tuningprototype.models.IndividualStockTrade;

import java.util.List;
import java.util.Map;

// Api response for retrieving stock trade data
public record StockTradeDataResponse(
        // Map of stock ticker to list of individual trades
        Map<String, List<IndividualStockTrade>> stockTrades,
        // Start time, defaults to current time if null
        Long startTime,
        // End time, defaults to start time + 15 minutes if null
        Long endTime) {}
