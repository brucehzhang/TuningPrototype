package com.tuning.tuningprototype.models.responses;

import com.tuning.tuningprototype.models.IndividualStockTrade;

import java.util.List;
import java.util.Map;

public record StockTradeDataResponse(
        Map<String, List<IndividualStockTrade>> stockTrades) {

}
