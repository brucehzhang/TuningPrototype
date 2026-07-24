package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.IndividualStockTrade;

import java.util.List;
import java.util.Map;

public interface IMarketAnalysisService {

    Map<String, List<IndividualStockTrade>> getHistoricalStockTradeData(List<String> tickers, long startTimestamp, long endTimestamp);
}
