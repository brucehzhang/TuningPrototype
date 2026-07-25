package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.exceptions.MarketDataException;
import com.tuning.tuningprototype.models.IndividualNews;
import com.tuning.tuningprototype.models.IndividualStockAggregate;
import com.tuning.tuningprototype.models.IndividualStockTrade;
import markets.alpaca.client.AlpacaClient;
import markets.alpaca.client.data.StockTradesRequest;
import markets.alpaca.client.openapi.data.api.NewsApi;
import markets.alpaca.client.openapi.data.api.StockApi;
import markets.alpaca.client.openapi.data.http.ApiException;
import markets.alpaca.client.openapi.data.model.*;
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
    private final StockApi _stockApiClient;
    private final NewsApi _newsApiClient;

    public AlpacaMarketAnalysisService(@Qualifier("getAlpacaClient") AlpacaClient alpacaClient) {
        _alpacaClient = alpacaClient;
        _stockApiClient = new StockApi(_alpacaClient.newDataClient());
        _newsApiClient = new NewsApi(_alpacaClient.newDataClient());
    }

    /**
     * Returns map of all trades for the given tickers and start and end times.
     *
     * @param tickers Tickers for checking the trades
     * @param startTimestamp Start timestamp in unix time
     * @param endTimestamp End timestamp in unix time
     * @return Map of tickers to the list of trades in the timespan
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
        return new IndividualStockTrade(stockTrade.getI(),
                stockTrade.getP(),
                stockTrade.getS(),
                stockTrade.getT(),
                exchangeMapper(stockTrade.getZ()));
    }

    // Gets the exchange group string based on the stock tape type
    private String exchangeMapper(StockTape stockType) {
        return switch (stockType) {
            case A -> "New York Stock Exchange";
            case B -> "NYSE Arca, Bats, IEX and other regional exchanges";
            case C -> "NASDAQ";
            case N -> "Overnight";
            case O -> "OTC";
            default -> "UNKNOWN";
        };
    }


    /**
     * Paginated retrieval of historical stock data.
     *
     * @param tickers Tickers that are being retrieved
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

    /**
     * Returns map of all stock aggregates for the given tickers and start and end times.
     *
     * @param tickers Tickers for checking the trades
     * @param timeframe The timeframe for each individual aggregation ([1-59]Min, [1-24]Hour, 1Day, 1Week, [1,2,3,4,6,12]Month)
     * @param startTimestamp Start timestamp in unix time
     * @param endTimestamp End timestamp in unix time
     * @return Map of tickers to the list of trades in the timespan
     */
    public Map<String, List<IndividualStockAggregate>> getHistoricalStockAggregateData(List<String> tickers, String timeframe, long startTimestamp, long endTimestamp) {
        try {
            StockBarsResp stockBars = getHistoricalStockAggregateDataWithPagination(tickers, timeframe, startTimestamp, endTimestamp, null);
            Map<String, List<IndividualStockAggregate>> aggregateMap = new HashMap<>();
            for (Map.Entry<String, List<StockBar>> stockBarsEntry : stockBars.getBars().entrySet()) {
                aggregateMap.put(stockBarsEntry.getKey(), stockBarsEntry.getValue().stream()
                        .map(this::individualStockAggregateMapper)
                        .collect(Collectors.toCollection(ArrayList::new)));
            }
            while (stockBars.getNextPageToken() != null) {
                stockBars = getHistoricalStockAggregateDataWithPagination(tickers, timeframe, startTimestamp, endTimestamp, stockBars.getNextPageToken());
                for (Map.Entry<String, List<StockBar>> stockBarsEntry : stockBars.getBars().entrySet()) {
                    aggregateMap.putIfAbsent(stockBarsEntry.getKey(), new ArrayList<>());
                    aggregateMap.get(stockBarsEntry.getKey()).addAll(stockBarsEntry.getValue().stream()
                            .map(this::individualStockAggregateMapper)
                            .collect(Collectors.toCollection(ArrayList::new)));
                }
            }
            return aggregateMap;
        } catch (ApiException apiException) {
            // TODO:: Proper logging
            System.out.println(apiException.getMessage());
            // Categorize exception into general MarketDataException.
            throw new MarketDataException(apiException.getMessage());
        }
    }

    // Converts Alpaca StockBar model to readable IndividualStockAggregate model to be in Market Data API response
    private IndividualStockAggregate individualStockAggregateMapper(StockBar stockBar) {
        return new IndividualStockAggregate(
                stockBar.getC(),
                stockBar.getH(),
                stockBar.getL(),
                stockBar.getN(),
                stockBar.getO(),
                stockBar.getT(),
                stockBar.getV(),
                stockBar.getVw()
        );
    }

    /**
     * Paginated retrieval of historical stock aggregate data.
     *
     * @param tickers Tickers that are being retrieved
     * @param timeframe The timeframe for each individual aggregation ([1-59]Min, [1-24]Hour, 1Day, 1Week, [1,2,3,4,6,12]Month)
     * @param startTimestamp Start time of historical search
     * @param endTimestamp End time of historical search
     * @param paginationToken paginationToken, can be null for first search
     * @return Response of all the requested stock trades in the provided window and pagination context
     * @throws ApiException Exception occurring during API call through Alpaca client
     */
    public StockBarsResp getHistoricalStockAggregateDataWithPagination(List<String> tickers, String timeframe, long startTimestamp, long endTimestamp, String paginationToken) throws ApiException {
        OffsetDateTime start = Instant.ofEpochSecond(startTimestamp)
                .atOffset(ZoneOffset.UTC);
        OffsetDateTime end = Instant.ofEpochSecond(endTimestamp)
                .atOffset(ZoneOffset.UTC);

        return _stockApiClient.stockBars(String.join(",", tickers), timeframe, start, end, 1000,
                null,
                null,
                null,
                null,
                paginationToken,
                Sort.ASC);
    }
}
