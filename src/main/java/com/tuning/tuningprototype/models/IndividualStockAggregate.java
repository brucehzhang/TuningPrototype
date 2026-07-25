package com.tuning.tuningprototype.models;

import java.time.OffsetDateTime;

// Individual stock details aggregated by timeframe in a more easily readable format
public record IndividualStockAggregate(
        Double closingPrice,
        Double highPrice,
        Double lowPrice,
        Long tradeCount,
        Double openingPrice,
        OffsetDateTime dateTime,
        Long volume,
        Double volumeWeightedAveragePrice
) {
}
