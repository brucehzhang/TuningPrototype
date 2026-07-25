package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.models.requests.NewsDataRequest;
import com.tuning.tuningprototype.models.requests.StockAggregateDataRequest;
import com.tuning.tuningprototype.models.requests.StockTradeDataRequest;
import com.tuning.tuningprototype.models.responses.NewsDataResponse;
import com.tuning.tuningprototype.models.responses.StockAggregateDataResponse;
import com.tuning.tuningprototype.models.responses.StockTradeDataResponse;
import com.tuning.tuningprototype.services.IMarketAnalysisService;
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

    @PostMapping("/stocks/trades")
    public ResponseEntity<?> fetchStockTradeData(@RequestBody StockTradeDataRequest stockTradeDataRequest) {
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
            return ResponseEntity.badRequest().body(String.format("Error getting stock trade data: %s", e.getMessage()));
        }
    }

    @PostMapping("/stocks/aggregates")
    public ResponseEntity<?> fetchStockTradeData(@RequestBody StockAggregateDataRequest stockAggregateDataRequest) {
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
            return ResponseEntity.badRequest().body(String.format("Error getting stock aggregate data: %s", e.getMessage()));
        }
    }

    @PostMapping("/news")
    public ResponseEntity<?> fetchNewsData(@RequestBody NewsDataRequest newsDataRequest) {
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
            return ResponseEntity.badRequest().body(String.format("Error getting news data: %s", e.getMessage()));
        }
    }

}
