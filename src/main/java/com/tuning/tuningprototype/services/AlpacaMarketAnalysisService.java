package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.exceptions.MarketDataException;
import com.tuning.tuningprototype.models.IndividualStockTrade;
import com.tuning.tuningprototype.models.responses.StockTradeDataResponse;
import markets.alpaca.client.AlpacaClient;
import markets.alpaca.client.data.StockTradesRequest;
import markets.alpaca.client.openapi.data.http.ApiException;
import markets.alpaca.client.openapi.data.model.Sort;
import markets.alpaca.client.openapi.data.model.StockHistoricalFeed;
import markets.alpaca.client.openapi.data.model.StockTrade;
import markets.alpaca.client.openapi.data.model.StockTradesResp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("alpacaMarketAnalysisService")
public class AlpacaMarketAnalysisService implements IMarketAnalysisService {

    private final AlpacaClient _alpacaClient;

    public AlpacaMarketAnalysisService(@Qualifier("getAlpacaClient") AlpacaClient alpacaClient) {
        _alpacaClient = alpacaClient;
    }

    /**
     * Returns map of all trades for the given tickets and start and end times.
     * @param tickers
     * @param startTimestamp
     * @param endTimestamp
     * @return
     */
    public Map<String, List<IndividualStockTrade>> getHistoricalStockTradeData(List<String> tickers, long startTimestamp, long endTimestamp) {
        try {
            StockTradesResp trades = getHistoricalStockTradeDataWithPagination(tickers, startTimestamp, endTimestamp, null);
            Map<String, List<IndividualStockTrade>> tradeMap = new HashMap<>();
            for (Map.Entry<String, List<StockTrade>> stockTradeEntry : trades.getTrades().entrySet()) {
                tradeMap.put(stockTradeEntry.getKey(), stockTradeEntry.getValue().stream()
                        .map(this::individualStockTradeMapper)
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
            while (trades.getNextPageToken() != null) {
                trades = getHistoricalStockTradeDataWithPagination(tickers, startTimestamp, endTimestamp, trades.getNextPageToken());
                for (Map.Entry<String, List<StockTrade>> stockTradeEntry : trades.getTrades().entrySet()) {
                    tradeMap.putIfAbsent(stockTradeEntry.getKey(), new ArrayList<>());
                    tradeMap.get(stockTradeEntry.getKey()).addAll(stockTradeEntry.getValue().stream()
                            .map(this::individualStockTradeMapper)
                            .collect(Collectors.toCollection(ArrayList::new)));
                }
            }
            return tradeMap;
        } catch (ApiException apiException) {
            // TODO:: Proper logging
            System.out.println(apiException.getMessage());
            // Categorize exception into general MarketDataException.
            throw new MarketDataException(apiException.getMessage());
        }
    }

    // Converts Alpaca StockTrade model to readable IndividualStockTrade model to be in Market Data API response
    private IndividualStockTrade individualStockTradeMapper(StockTrade stockTrade) {
        //Long price,
        //        Long tradeSize,
        //        String dateTime,
        //        String exchange
        IndividualStockTrade individualStockTrade = new IndividualStockTrade(stockTrade.getI(), stockTrade.getP().longValue(), )
    }


    /**
     * Paginated retrieval of historical stock data.
     *
     * @param tickers Tickets that are being retrieved
     * @param startTimestamp Start time of historical search
     * @param endTimestamp End time of historical search
     * @param paginationToken paginationToken, can be null for first search
     * @return Response of all the requested stock trades in the provided window and pagination context
     * @throws ApiException Exception occurring during API call through Alpaca client
     */
    public StockTradesResp getHistoricalStockTradeDataWithPagination(List<String> tickers, long startTimestamp, long endTimestamp, String paginationToken) throws ApiException {
        OffsetDateTime start = Instant.ofEpochSecond(startTimestamp)
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime end = Instant.ofEpochSecond(endTimestamp)
                .atOffset(ZoneOffset.UTC);

        return _alpacaClient.stocks().trades(StockTradesRequest.builder()
                .symbols(tickers)
                .start(start)
                .end(end)
                .feed(StockHistoricalFeed.IEX)
                .sort(Sort.ASC)
                .limit(1000)
                .pageToken(paginationToken)
                .build());
    }

    public StockTradeDataResponse getHistoricalStockAggregateData(List<String> tickers, long startTimestamp, long endTimestamp) {
        try {
            StockTradesResp trades = getHistoricalStockTradeDataWithPagination(tickers, startTimestamp, endTimestamp, null);
            Map<String, List<StockTrade>> tradeMap = new HashMap<>(trades.getTrades());
            while (trades.getNextPageToken() != null) {
                trades = getHistoricalStockTradeDataWithPagination(tickers, startTimestamp, endTimestamp, trades.getNextPageToken());
                tradeMap.putAll(trades.getTrades());
            }
            return new StockTradeDataResponse(tradeMap, trades.getCurrency());
        } catch (ApiException apiException) {
            // TODO:: Proper logging
            System.out.println(apiException.getMessage());
            // Categorize exception into general MarketDataException.
            throw new MarketDataException(apiException.getMessage());
        }
    }

    /**
     * Paginated retrieval of historical stock aggregate data.
     *
     * @param tickers Tickets that are being retrieved
     * @param startTimestamp Start time of historical search
     * @param endTimestamp End time of historical search
     * @param paginationToken paginationToken, can be null for first search
     * @return Response of all the requested stock trades in the provided window and pagination context
     * @throws ApiException Exception occurring during API call through Alpaca client
     */
    public StockTradesResp getHistoricalStockAggregateDataWithPagination(List<String> tickers, long startTimestamp, long endTimestamp, String paginationToken) throws ApiException {
        OffsetDateTime start = Instant.ofEpochSecond(startTimestamp)
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime end = Instant.ofEpochSecond(endTimestamp)
                .atOffset(ZoneOffset.UTC);

        return _alpacaClient.stocks().trades(StockTradesRequest.builder()
                .symbols(tickers)
                .start(start)
                .end(end)
                .feed(StockHistoricalFeed.IEX)
                .sort(Sort.ASC)
                .limit(1000)
                .pageToken(paginationToken)
                .build());
    }
}
