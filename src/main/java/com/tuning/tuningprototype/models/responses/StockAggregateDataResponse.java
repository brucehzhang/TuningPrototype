package com.tuning.tuningprototype.models.responses;

import com.tuning.tuningprototype.models.IndividualStockAggregate;

import java.util.List;
import java.util.Map;

// Api response for retrieving stock aggregate data
public record StockAggregateDataResponse(
        // Map of stock ticker to list of aggregated stock prices and details (high, low, etc)
        Map<String, List<IndividualStockAggregate>> stockAggregates,
        // The timeframe for dividing aggregations ([1-59]Min, [1-24]Hour, 1Day, 1Week, [1,2,3,4,6,12]Month)
        String timeframe,
        // Start time, defaults to current time if null
        Long startTime,
        // End time, defaults to start time + 15 minutes if null
        Long endTime) {}