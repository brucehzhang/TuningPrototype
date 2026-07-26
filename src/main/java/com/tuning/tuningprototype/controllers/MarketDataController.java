package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.models.requests.NewsDataRequest;
import com.tuning.tuningprototype.models.requests.StockAggregateDataRequest;
import com.tuning.tuningprototype.models.requests.StockTradeDataRequest;
import com.tuning.tuningprototype.models.responses.NewsDataResponse;
import com.tuning.tuningprototype.models.responses.StockAggregateDataResponse;
import com.tuning.tuningprototype.models.responses.StockTradeDataResponse;
import com.tuning.tuningprototype.services.IMarketAnalysisService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping(value = "/marketData", produces = MediaType.APPLICATION_JSON_VALUE)
public class MarketDataController {

    private final IMarketAnalysisService _alpacaMarketAnalysisService;

    public MarketDataController(
            @Qualifier("alpacaMarketAnalysisService") IMarketAnalysisService alpacaMarketAnalysisService) {
        _alpacaMarketAnalysisService = alpacaMarketAnalysisService;
    }

    /**
     * Api and MCP tool for fetching individual stock trades based on provided tickers in the provided start and end time window.
     * The data size can be large, so smaller time windows of 1 hour is recommended
     *
     * @param stockTradeDataRequest Request payload with tickers, start, and end time
     * @return 200 with tickers mapped to individual stock trades, 500 if server error
     */
    @PostMapping("/stocks/trades")
    @McpTool(description = "Fetches all of the individual trades for the requested stock tickers that have occurred between the start and end time window.")
    public ResponseEntity<?> fetchStockTradeData(
            @McpToolParam(description = "Structured object containing list of tickers (ex. GOOG, AAPL), start time in unix time, and end time in unix time")
            @RequestBody StockTradeDataRequest stockTradeDataRequest) {
        try {
            // TODO:: More validations, probably shared helper method
            long startTime = stockTradeDataRequest.startTime() != null
                    ? stockTradeDataRequest.startTime()
                    : Instant.now().minus(15, ChronoUnit.MINUTES).getEpochSecond();
            long endTime = stockTradeDataRequest.endTime() != null
                    ? stockTradeDataRequest.endTime()
                    : Instant.ofEpochSecond(startTime).plus(15, ChronoUnit.MINUTES).getEpochSecond();
            return ResponseEntity.ok(
                    new StockTradeDataResponse(_alpacaMarketAnalysisService.getStockTradeData(
                            stockTradeDataRequest.tickers(), startTime, endTime), startTime, endTime));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(String.format("Error getting stock trade data: %s", e.getMessage()));
        }
    }

    /**
     * Api and MCP tool for fetching stock based on provided tickers aggregated by timeframe in the provided start and end time window.
     *
     * @param stockAggregateDataRequest Request payload with tickers, timeframe for the aggregate, start, and end time
     * @return 200 with tickers mapped to stock data aggregated in the user provided timeframe, 500 if server error
     */
    @PostMapping("/stocks/aggregates")
    @McpTool(description = "Fetches stock price details aggregated by a provided timeframe for the requested stock tickers that have occurred between the start and end time window.")
    public ResponseEntity<?> fetchStockAggregateData(
            @McpToolParam(description = "Structured object containing list of tickers (ex. GOOG, AAPL), timeframe for each aggregated stock price detail (Ex: [1-59]Min, [1-24]Hour, 1Day, 1Week, [1,2,3,4,6,12]Month), start time in unix time, and end time in unix time")
            @RequestBody StockAggregateDataRequest stockAggregateDataRequest) {
        try {
            // TODO:: More validations, probably shared helper method
            long startTime = stockAggregateDataRequest.startTime() != null
                    ? stockAggregateDataRequest.startTime()
                    : Instant.now().minus(15, ChronoUnit.MINUTES).getEpochSecond();
            long endTime = stockAggregateDataRequest.endTime() != null
                    ? stockAggregateDataRequest.endTime()
                    : Instant.ofEpochSecond(startTime).plus(15, ChronoUnit.MINUTES).getEpochSecond();
            return ResponseEntity.ok(
                    new StockAggregateDataResponse(_alpacaMarketAnalysisService.getStockAggregateData(
                            stockAggregateDataRequest.tickers(), stockAggregateDataRequest.timeframe(), startTime, endTime),
                            stockAggregateDataRequest.timeframe(), startTime, endTime));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(String.format("Error getting stock aggregate data: %s", e.getMessage()));
        }
    }

    /**
     * Api and MCP tool for fetching news data based on provided tickers in the start and end time window.
     *
     * @param newsDataRequest Request payload with tickers, start, and end time
     * @return 200 with tickers mapped to news articles, 500 if server error
     */
    @PostMapping("/news")
    @McpTool(description = "Fetches financial news for the requested stock tickers that have occurred between the start and end time window.")
    public ResponseEntity<?> fetchNewsData(
            @McpToolParam(description = "Structured object containing list of tickers (ex. GOOG, AAPL), start time in unix time, and end time in unix time")
            @RequestBody NewsDataRequest newsDataRequest) {
        try {
            // TODO:: More validations, probably shared helper method
            long startTime = newsDataRequest.startTime() != null
                    ? newsDataRequest.startTime()
                    : Instant.now().minus(15, ChronoUnit.MINUTES).getEpochSecond();
            long endTime = newsDataRequest.endTime() != null
                    ? newsDataRequest.endTime()
                    : Instant.ofEpochSecond(startTime).plus(15, ChronoUnit.MINUTES).getEpochSecond();
            return ResponseEntity.ok(
                    new NewsDataResponse(_alpacaMarketAnalysisService.getNewsData(
                            newsDataRequest.tickers(), startTime, endTime), startTime, endTime));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(String.format("Error getting news data: %s", e.getMessage()));
        }
    }

}
