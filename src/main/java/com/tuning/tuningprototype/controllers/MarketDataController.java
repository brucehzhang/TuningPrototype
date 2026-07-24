package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.models.requests.StockTradeDataRequest;
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

    @PostMapping("/historical/stocks/trades")
    public ResponseEntity<?> fetchHistoricalStockTradeData(@RequestBody StockTradeDataRequest stockTradeDataRequest) {
        try {
            // TODO:: More validations, probably shared helper method
            long startTime = stockTradeDataRequest.startTime() != null
                    ? stockTradeDataRequest.startTime()
                    : Instant.now().minus(15, ChronoUnit.MINUTES).getEpochSecond();
            long endTime = stockTradeDataRequest.endTime() != null
                    ? stockTradeDataRequest.endTime()
                    : Instant.ofEpochSecond(startTime).plus(15, ChronoUnit.MINUTES).getEpochSecond();
            return ResponseEntity.ok(
                    new StockTradeDataResponse(_alpacaMarketAnalysisService.getHistoricalStockTradeData(
                            stockTradeDataRequest.tickers(), startTime, endTime)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(String.format("Error getting historical stock trade data: %s", e.getMessage()));
        }
    }
}
