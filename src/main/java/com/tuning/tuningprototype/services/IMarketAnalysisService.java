package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.IndividualStockAggregate;
import com.tuning.tuningprototype.models.IndividualStockTrade;

import java.util.List;
import java.util.Map;

public interface IMarketAnalysisService {

    Map<String, List<IndividualStockTrade>> getHistoricalStockTradeData(List<String> tickers, long startTimestamp, long endTimestamp);

    Map<String, List<IndividualStockAggregate>> getHistoricalStockAggregateData(List<String> tickers, String timeframe, long startTimestamp, long endTimestamp);
}
