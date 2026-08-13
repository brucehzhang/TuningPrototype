package com.tuning.tuningprototype.services.integration;

import com.tuning.tuningprototype.models.IndividualNewsArticle;
import com.tuning.tuningprototype.models.IndividualStockAggregate;
import com.tuning.tuningprototype.models.IndividualStockTrade;

import java.util.List;
import java.util.Map;

public interface IMarketAnalysisService {

    Map<String, List<IndividualStockTrade>> getStockTradeData(List<String> tickers, long startTimestamp, long endTimestamp);

    Map<String, List<IndividualStockAggregate>> getStockAggregateData(List<String> tickers, String timeframe, long startTimestamp, long endTimestamp);

    Map<String, List<IndividualNewsArticle>> getNewsData(List<String> tickers, long startTimestamp, long endTimestamp);
}
