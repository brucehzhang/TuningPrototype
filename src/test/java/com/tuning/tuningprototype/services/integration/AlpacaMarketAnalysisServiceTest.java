package com.tuning.tuningprototype.services.integration;

import com.tuning.tuningprototype.exceptions.MarketDataException;
import com.tuning.tuningprototype.models.IndividualNewsArticle;
import com.tuning.tuningprototype.models.IndividualStockAggregate;
import com.tuning.tuningprototype.models.IndividualStockTrade;
import markets.alpaca.client.AlpacaClient;
import markets.alpaca.client.openapi.data.http.ApiClient;
import markets.alpaca.client.openapi.data.http.ApiException;
import markets.alpaca.client.openapi.data.http.ApiResponse;
import markets.alpaca.client.openapi.data.model.*;
import okhttp3.Call;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * _stockApiClient/_newsApiClient are built internally in the service's constructor from
 * alpacaClient.newDataClient() — they aren't injected, so they can't be mocked directly.
 * Instead we hand the service a spy() over a real ApiClient and stub only its execute(...)
 * method (the sole point that would otherwise perform network I/O); buildCall(...) and the
 * rest of the generated client run for real but never touch the network since we intercept
 * before Call.execute() would be invoked.
 */
@ExtendWith(MockitoExtension.class)
class AlpacaMarketAnalysisServiceTest {

    @Mock
    private AlpacaClient alpacaClient;
    @Mock
    private AlpacaClient.Stocks stocksFacade;

    private ApiClient dataApiClientSpy;
    private AlpacaMarketAnalysisService service;

    @BeforeEach
    void setUp() {
        dataApiClientSpy = spy(new ApiClient());
        when(alpacaClient.newDataClient()).thenReturn(dataApiClientSpy);
        service = new AlpacaMarketAnalysisService(alpacaClient);
    }

    // --- getStockTradeData (goes through AlpacaClient.stocks().trades(...)) ---

    @Test
    void getStockTradeData_singlePage_mapsAllFieldsAndExchangeGroups() throws ApiException {
        when(alpacaClient.stocks()).thenReturn(stocksFacade);
        OffsetDateTime tradeTime = OffsetDateTime.now();
        StockTrade nyse = new StockTrade().i(1).p(100.5).s(10).t(tradeTime).z(StockTape.A);
        StockTrade arca = new StockTrade().i(2).p(101.0).s(20).t(tradeTime).z(StockTape.B);
        StockTrade nasdaq = new StockTrade().i(3).p(102.0).s(30).t(tradeTime).z(StockTape.C);
        StockTrade overnight = new StockTrade().i(4).p(103.0).s(40).t(tradeTime).z(StockTape.N);
        StockTrade otc = new StockTrade().i(5).p(104.0).s(50).t(tradeTime).z(StockTape.O);
        StockTradesResp resp = new StockTradesResp()
                .trades(Map.of("AAPL", List.of(nyse, arca, nasdaq, overnight, otc)));
        when(stocksFacade.trades(any())).thenReturn(resp);

        Map<String, List<IndividualStockTrade>> result = service.getStockTradeData(List.of("AAPL"), 1000L, 2000L);

        assertThat(result.get("AAPL")).hasSize(5);
        assertThat(result.get("AAPL")).extracting(IndividualStockTrade::exchangeGroup)
                .containsExactlyInAnyOrder(
                        "New York Stock Exchange",
                        "NYSE Arca, Bats, IEX and other regional exchanges",
                        "NASDAQ",
                        "Overnight",
                        "OTC");
        IndividualStockTrade mapped = result.get("AAPL").stream()
                .filter(t -> t.tradeId() == 1L).findFirst().orElseThrow();
        assertThat(mapped.price()).isEqualTo(100.5);
        assertThat(mapped.tradeSize()).isEqualTo(10L);
        assertThat(mapped.dateTime()).isEqualTo(tradeTime);
    }

    @Test
    void getStockTradeData_multiplePages_mergesResultsAcrossPages() throws ApiException {
        when(alpacaClient.stocks()).thenReturn(stocksFacade);
        StockTrade firstPageTrade = new StockTrade().i(1).p(100.0).s(10).t(OffsetDateTime.now()).z(StockTape.A);
        StockTradesResp page1 = new StockTradesResp()
                .trades(Map.of("AAPL", List.of(firstPageTrade)))
                .nextPageToken("token-1");
        StockTrade secondPageTrade = new StockTrade().i(2).p(200.0).s(20).t(OffsetDateTime.now()).z(StockTape.C);
        StockTradesResp page2 = new StockTradesResp()
                .trades(Map.of("AAPL", List.of(secondPageTrade), "TSLA", List.of(secondPageTrade)));
        when(stocksFacade.trades(any())).thenReturn(page1, page2);

        Map<String, List<IndividualStockTrade>> result = service.getStockTradeData(List.of("AAPL", "TSLA"), 1000L, 2000L);

        assertThat(result.get("AAPL")).hasSize(2);
        assertThat(result.get("TSLA")).hasSize(1);
        verify(stocksFacade, times(2)).trades(any());
    }

    @Test
    void getStockTradeData_apiException_throwsMarketDataException() throws ApiException {
        when(alpacaClient.stocks()).thenReturn(stocksFacade);
        when(stocksFacade.trades(any())).thenThrow(new ApiException("trade lookup failed"));

        assertThatThrownBy(() -> service.getStockTradeData(List.of("AAPL"), 1000L, 2000L))
                .isInstanceOf(MarketDataException.class)
                .hasMessageContaining("trade lookup failed");
    }

    @Test
    void getStockTradeDataWithPagination_delegatesToAlpacaClientStocks() throws ApiException {
        when(alpacaClient.stocks()).thenReturn(stocksFacade);
        StockTradesResp resp = new StockTradesResp().trades(Map.of());
        when(stocksFacade.trades(any())).thenReturn(resp);

        StockTradesResp result = service.getStockTradeDataWithPagination(List.of("AAPL"), 1000L, 2000L, null);

        assertThat(result).isSameAs(resp);
    }

    // --- getStockAggregateData (goes through the internally-built StockApi) ---

    @Test
    void getStockAggregateData_singlePage_mapsAllFields() {
        OffsetDateTime barTime = OffsetDateTime.now();
        StockBar bar = new StockBar().c(150.0).h(155.0).l(145.0).n(1000L).o(148.0).t(barTime).v(50000L).vw(149.5);
        StockBarsResp resp = new StockBarsResp().putBarsItem("AAPL", List.of(bar));
        stubExecute(new ApiResponse<>(200, Map.of(), resp));

        Map<String, List<IndividualStockAggregate>> result =
                service.getStockAggregateData(List.of("AAPL"), "1Day", 1000L, 2000L);

        assertThat(result.get("AAPL")).hasSize(1);
        IndividualStockAggregate agg = result.get("AAPL").get(0);
        assertThat(agg.closingPrice()).isEqualTo(150.0);
        assertThat(agg.highPrice()).isEqualTo(155.0);
        assertThat(agg.lowPrice()).isEqualTo(145.0);
        assertThat(agg.tradeCount()).isEqualTo(1000L);
        assertThat(agg.openingPrice()).isEqualTo(148.0);
        assertThat(agg.dateTime()).isEqualTo(barTime);
        assertThat(agg.volume()).isEqualTo(50000L);
        assertThat(agg.volumeWeightedAveragePrice()).isEqualTo(149.5);
    }

    @Test
    void getStockAggregateData_multiplePages_mergesResultsAcrossPages() throws ApiException {
        StockBar bar1 = new StockBar().c(1.0).h(1.0).l(1.0).n(1L).o(1.0).t(OffsetDateTime.now()).v(1L).vw(1.0);
        StockBarsResp page1 = new StockBarsResp().putBarsItem("AAPL", List.of(bar1)).nextPageToken("token-1");
        StockBar bar2 = new StockBar().c(2.0).h(2.0).l(2.0).n(2L).o(2.0).t(OffsetDateTime.now()).v(2L).vw(2.0);
        StockBarsResp page2 = new StockBarsResp().putBarsItem("AAPL", List.of(bar2));
        doReturn(new ApiResponse<>(200, Map.of(), page1))
                .doReturn(new ApiResponse<>(200, Map.of(), page2))
                .when(dataApiClientSpy).execute(any(Call.class), any(Type.class));

        Map<String, List<IndividualStockAggregate>> result =
                service.getStockAggregateData(List.of("AAPL"), "1Day", 1000L, 2000L);

        assertThat(result.get("AAPL")).hasSize(2);
        verify(dataApiClientSpy, times(2)).execute(any(Call.class), any(Type.class));
    }

    @Test
    void getStockAggregateData_apiException_throwsMarketDataException() throws ApiException {
        doThrow(new ApiException("aggregate lookup failed"))
                .when(dataApiClientSpy).execute(any(Call.class), any(Type.class));

        assertThatThrownBy(() -> service.getStockAggregateData(List.of("AAPL"), "1Day", 1000L, 2000L))
                .isInstanceOf(MarketDataException.class)
                .hasMessageContaining("aggregate lookup failed");
    }

    @Test
    void getStockAggregateDataWithPagination_returnsRawResponse() throws ApiException {
        StockBarsResp resp = new StockBarsResp();
        stubExecute(new ApiResponse<>(200, Map.of(), resp));

        StockBarsResp result = service.getStockAggregateDataWithPagination(List.of("AAPL"), "1Day", 1000L, 2000L, null);

        assertThat(result).isSameAs(resp);
    }

    // --- getNewsData (goes through the internally-built NewsApi) ---

    @Test
    void getNewsData_filtersArticlesToRequestedTickersOnly() {
        News aaplNews = new News().author("author").content("content").createdAt(OffsetDateTime.now())
                .headline("headline").id(1L).images(Set.of()).source("source").summary("summary")
                .symbols(List.of("AAPL", "UNREQUESTED")).updatedAt(OffsetDateTime.now());
        NewsResp resp = new NewsResp().news(List.of(aaplNews));
        stubExecute(new ApiResponse<>(200, Map.of(), resp));

        Map<String, List<IndividualNewsArticle>> result = service.getNewsData(List.of("AAPL", "TSLA"), 1000L, 2000L);

        assertThat(result).containsKeys("AAPL", "TSLA");
        assertThat(result.get("AAPL")).hasSize(1);
        assertThat(result.get("AAPL").get(0).headline()).isEqualTo("headline");
        assertThat(result.get("TSLA")).isEmpty();
        // UNREQUESTED is on the article but wasn't asked for, so no bucket should be created for it.
        assertThat(result).doesNotContainKey("UNREQUESTED");
    }

    @Test
    void getNewsData_multiplePages_mergesResultsAcrossPages() throws ApiException {
        News page1News = new News().author("a").content("c").createdAt(OffsetDateTime.now())
                .headline("h1").id(1L).images(Set.of()).source("s").summary("sum")
                .symbols(List.of("AAPL")).updatedAt(OffsetDateTime.now());
        NewsResp page1 = new NewsResp().news(List.of(page1News)).nextPageToken("token-1");
        News page2News = new News().author("a").content("c").createdAt(OffsetDateTime.now())
                .headline("h2").id(2L).images(Set.of()).source("s").summary("sum")
                .symbols(List.of("AAPL")).updatedAt(OffsetDateTime.now());
        NewsResp page2 = new NewsResp().news(List.of(page2News));
        doReturn(new ApiResponse<>(200, Map.of(), page1))
                .doReturn(new ApiResponse<>(200, Map.of(), page2))
                .when(dataApiClientSpy).execute(any(Call.class), any(Type.class));

        Map<String, List<IndividualNewsArticle>> result = service.getNewsData(List.of("AAPL"), 1000L, 2000L);

        assertThat(result.get("AAPL")).hasSize(2);
        verify(dataApiClientSpy, times(2)).execute(any(Call.class), any(Type.class));
    }

    @Test
    void getNewsData_apiException_throwsMarketDataException() throws ApiException {
        doThrow(new ApiException("news lookup failed"))
                .when(dataApiClientSpy).execute(any(Call.class), any(Type.class));

        assertThatThrownBy(() -> service.getNewsData(List.of("AAPL"), 1000L, 2000L))
                .isInstanceOf(MarketDataException.class)
                .hasMessageContaining("news lookup failed");
    }

    @Test
    void getNewsDataWithPagination_returnsRawResponse() throws ApiException {
        NewsResp resp = new NewsResp();
        stubExecute(new ApiResponse<>(200, Map.of(), resp));

        NewsResp result = service.getNewsDataWithPagination(List.of("AAPL"), 1000L, 2000L, null);

        assertThat(result).isSameAs(resp);
    }

    @SuppressWarnings("unchecked")
    private void stubExecute(ApiResponse<?> response) {
        try {
            doReturn(response).when(dataApiClientSpy).execute(any(Call.class), any(Type.class));
        } catch (ApiException e) {
            throw new AssertionError(e);
        }
    }
}
