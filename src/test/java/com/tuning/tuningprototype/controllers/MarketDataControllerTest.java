package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.models.IndividualNewsArticle;
import com.tuning.tuningprototype.models.IndividualStockAggregate;
import com.tuning.tuningprototype.models.IndividualStockTrade;
import com.tuning.tuningprototype.models.requests.NewsDataRequest;
import com.tuning.tuningprototype.models.requests.StockAggregateDataRequest;
import com.tuning.tuningprototype.models.requests.StockTradeDataRequest;
import com.tuning.tuningprototype.models.responses.ErrorResponse;
import com.tuning.tuningprototype.models.responses.NewsDataResponse;
import com.tuning.tuningprototype.models.responses.StockAggregateDataResponse;
import com.tuning.tuningprototype.models.responses.StockTradeDataResponse;
import com.tuning.tuningprototype.services.integration.IMarketAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketDataControllerTest {

    @Mock
    private IMarketAnalysisService marketAnalysisService;

    private MarketDataController controller;

    @BeforeEach
    void setUp() {
        controller = new MarketDataController(marketAnalysisService);
    }

    // --- fetchStockTradeData ---

    @Test
    void fetchStockTradeData_explicitTimes_returns200WithBody() {
        StockTradeDataRequest request = new StockTradeDataRequest(List.of("AAPL"), 100L, 200L);
        Map<String, List<IndividualStockTrade>> trades = Map.of("AAPL", List.of());
        when(marketAnalysisService.getStockTradeData(List.of("AAPL"), 100L, 200L)).thenReturn(trades);

        var response = controller.fetchStockTradeData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        StockTradeDataResponse body = (StockTradeDataResponse) response.getBody();
        assertThat(body.stockTrades()).isEqualTo(trades);
        assertThat(body.startTime()).isEqualTo(100L);
        assertThat(body.endTime()).isEqualTo(200L);
    }

    @Test
    void fetchStockTradeData_nullTimes_defaultsAreComputed() {
        StockTradeDataRequest request = new StockTradeDataRequest(List.of("AAPL"), null, null);
        when(marketAnalysisService.getStockTradeData(eq(List.of("AAPL")), anyLong(), anyLong()))
                .thenReturn(Map.of());

        var response = controller.fetchStockTradeData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        StockTradeDataResponse body = (StockTradeDataResponse) response.getBody();
        assertThat(body.startTime()).isNotNull();
        assertThat(body.endTime()).isEqualTo(body.startTime() + 900);
    }

    @Test
    void fetchStockTradeData_serviceThrows_returns500() {
        StockTradeDataRequest request = new StockTradeDataRequest(List.of("AAPL"), 100L, 200L);
        when(marketAnalysisService.getStockTradeData(List.of("AAPL"), 100L, 200L))
                .thenThrow(new RuntimeException("boom"));

        var response = controller.fetchStockTradeData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    // --- fetchStockAggregateData ---

    @Test
    void fetchStockAggregateData_explicitTimes_returns200WithBody() {
        StockAggregateDataRequest request = new StockAggregateDataRequest(List.of("AAPL"), "1Day", 100L, 200L);
        Map<String, List<IndividualStockAggregate>> aggregates = Map.of("AAPL", List.of());
        when(marketAnalysisService.getStockAggregateData(List.of("AAPL"), "1Day", 100L, 200L)).thenReturn(aggregates);

        var response = controller.fetchStockAggregateData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        StockAggregateDataResponse body = (StockAggregateDataResponse) response.getBody();
        assertThat(body.stockAggregates()).isEqualTo(aggregates);
        assertThat(body.timeframe()).isEqualTo("1Day");
        assertThat(body.startTime()).isEqualTo(100L);
        assertThat(body.endTime()).isEqualTo(200L);
    }

    @Test
    void fetchStockAggregateData_nullTimes_defaultsAreComputed() {
        StockAggregateDataRequest request = new StockAggregateDataRequest(List.of("AAPL"), "1Day", null, null);
        when(marketAnalysisService.getStockAggregateData(eq(List.of("AAPL")), eq("1Day"), anyLong(), anyLong()))
                .thenReturn(Map.of());

        var response = controller.fetchStockAggregateData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        StockAggregateDataResponse body = (StockAggregateDataResponse) response.getBody();
        assertThat(body.startTime()).isNotNull();
        assertThat(body.endTime()).isEqualTo(body.startTime() + 900);
    }

    @Test
    void fetchStockAggregateData_serviceThrows_returns500() {
        StockAggregateDataRequest request = new StockAggregateDataRequest(List.of("AAPL"), "1Day", 100L, 200L);
        when(marketAnalysisService.getStockAggregateData(List.of("AAPL"), "1Day", 100L, 200L))
                .thenThrow(new RuntimeException("boom"));

        var response = controller.fetchStockAggregateData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    // --- fetchNewsData ---

    @Test
    void fetchNewsData_explicitTimes_returns200WithBody() {
        NewsDataRequest request = new NewsDataRequest(List.of("AAPL"), 100L, 200L);
        Map<String, List<IndividualNewsArticle>> news = Map.of("AAPL", List.of());
        when(marketAnalysisService.getNewsData(List.of("AAPL"), 100L, 200L)).thenReturn(news);

        var response = controller.fetchNewsData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        NewsDataResponse body = (NewsDataResponse) response.getBody();
        assertThat(body.stockNewsArticles()).isEqualTo(news);
        assertThat(body.startTime()).isEqualTo(100L);
        assertThat(body.endTime()).isEqualTo(200L);
    }

    @Test
    void fetchNewsData_nullTimes_defaultsAreComputed() {
        NewsDataRequest request = new NewsDataRequest(List.of("AAPL"), null, null);
        when(marketAnalysisService.getNewsData(eq(List.of("AAPL")), anyLong(), anyLong()))
                .thenReturn(Map.of());

        var response = controller.fetchNewsData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        NewsDataResponse body = (NewsDataResponse) response.getBody();
        assertThat(body.startTime()).isNotNull();
        assertThat(body.endTime()).isEqualTo(body.startTime() + 900);
    }

    @Test
    void fetchNewsData_serviceThrows_returns500() {
        NewsDataRequest request = new NewsDataRequest(List.of("AAPL"), 100L, 200L);
        when(marketAnalysisService.getNewsData(List.of("AAPL"), 100L, 200L))
                .thenThrow(new RuntimeException("boom"));

        var response = controller.fetchNewsData(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }
}
