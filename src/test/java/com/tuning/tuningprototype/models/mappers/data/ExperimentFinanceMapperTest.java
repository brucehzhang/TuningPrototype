package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExperimentFinanceMapperTest {

    private final ExperimentFinanceMapper mapper = new ExperimentFinanceMapper();

    private ExperimentDto experimentDto(Long id) {
        return new ExperimentDto(id, "e", null, null, null, null, null, null, null, null, null, null, null);
    }

    private PurchaseLotDto purchaseLot(Long id, BigDecimal price, BigDecimal quantity, List<AssetSaleDto> sales) {
        return new PurchaseLotDto(id, 1L, 1L, "AAPL", price, quantity, 1000L, 1000L, 1000L, sales);
    }

    private AssetSaleDto sale(BigDecimal price, BigDecimal quantity) {
        return new AssetSaleDto(1L, 1L, 1L, "AAPL", price, quantity, 1000L, 1000L, 1000L);
    }

    @Test
    void toExperimentFinances_returnsNull_whenExperimentDtoIsNull() {
        assertThat(mapper.toExperimentFinances(null, List.of(), 5000L)).isNull();
    }

    @Test
    void toExperimentFinances_returnsEmptyWalletBalances_whenWalletDtosIsNull() {
        ExperimentFinances result = mapper.toExperimentFinances(experimentDto(1L), null, 5000L);

        assertThat(result.experimentId()).isEqualTo(1L);
        assertThat(result.asOf()).isEqualTo(5000L);
        assertThat(result.activeWalletBalances()).isEmpty();
    }

    @Test
    void toExperimentFinances_treatsNullPurchaseLots_asEmpty() {
        WalletDto wallet = new WalletDto(1L, 1L, new BigDecimal("1000.00"), 1000L, "USD", 1000L, 1000L, null);

        ExperimentFinances result = mapper.toExperimentFinances(experimentDto(1L), List.of(wallet), 5000L);

        ExperimentFinances.ActiveWalletBalance balance = result.activeWalletBalances().get(0);
        assertThat(balance.walletId()).isEqualTo(1L);
        assertThat(balance.startingMoneyAmount()).isEqualByComparingTo("1000.00");
        assertThat(balance.currentMoneyAmount()).isEqualByComparingTo("1000.00");
        assertThat(balance.currencyCode()).isEqualTo("USD");
        assertThat(balance.activePurchaseLots()).isEmpty();
    }

    @Test
    void toExperimentFinances_computesCurrentMoneyAmount_andFiltersFullySoldLots() {
        PurchaseLotDto lotA = purchaseLot(
                101L, new BigDecimal("10"), new BigDecimal("5"),
                List.of(sale(new BigDecimal("12"), new BigDecimal("2"))));
        PurchaseLotDto lotB = purchaseLot(
                102L, new BigDecimal("20"), new BigDecimal("4"),
                List.of(sale(new BigDecimal("25"), new BigDecimal("4"))));
        PurchaseLotDto lotC = purchaseLot(
                103L, new BigDecimal("5"), new BigDecimal("10"), null);

        WalletDto wallet = new WalletDto(
                1L, 1L, new BigDecimal("1000"), 1000L, "USD", 1000L, 1000L,
                List.of(lotA, lotB, lotC));

        ExperimentFinances result = mapper.toExperimentFinances(experimentDto(1L), List.of(wallet), 5000L);

        assertThat(result.experimentId()).isEqualTo(1L);
        assertThat(result.asOf()).isEqualTo(5000L);

        ExperimentFinances.ActiveWalletBalance balance = result.activeWalletBalances().get(0);
        // starting 1000 - (50 + 80 + 50) purchased + (24 + 100) sale proceeds = 944
        assertThat(balance.currentMoneyAmount()).isEqualByComparingTo("944");

        assertThat(balance.activePurchaseLots()).hasSize(2);
        assertThat(balance.activePurchaseLots())
                .extracting(ExperimentFinances.ActiveWalletBalance.ActivePurchaseLot::purchaseLotId)
                .containsExactly(101L, 103L);

        ExperimentFinances.ActiveWalletBalance.ActivePurchaseLot activeLotA = balance.activePurchaseLots().get(0);
        assertThat(activeLotA.ticker()).isEqualTo("AAPL");
        assertThat(activeLotA.purchasePrice()).isEqualByComparingTo("10");
        assertThat(activeLotA.currentQuantity()).isEqualByComparingTo("3");

        ExperimentFinances.ActiveWalletBalance.ActivePurchaseLot activeLotC = balance.activePurchaseLots().get(1);
        assertThat(activeLotC.currentQuantity()).isEqualByComparingTo("10");
    }

    @Test
    void toExperimentFinances_treatsNullSaleQuantity_asZeroProceeds() {
        PurchaseLotDto lot = purchaseLot(
                101L, new BigDecimal("10"), new BigDecimal("5"),
                List.of(sale(new BigDecimal("12"), null)));
        WalletDto wallet = new WalletDto(
                1L, 1L, new BigDecimal("1000"), 1000L, "USD", 1000L, 1000L, List.of(lot));

        ExperimentFinances result = mapper.toExperimentFinances(experimentDto(1L), List.of(wallet), 5000L);

        ExperimentFinances.ActiveWalletBalance balance = result.activeWalletBalances().get(0);
        // starting 1000 - 50 purchased + 0 sale proceeds (null quantity treated as zero) = 950
        assertThat(balance.currentMoneyAmount()).isEqualByComparingTo("950");
    }
}
