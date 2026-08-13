package com.tuning.tuningprototype.models.mappers.data;

import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.db.entity.AssetSaleDto;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.PurchaseLotDto;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ExperimentFinanceMapper {

    /**
     * Builds a point-in-time financial snapshot from an already-fetched ExperimentDto
     * and a list of WalletDtos whose purchaseLots (and each lot's assetSales) are
     * already populated. This mapper does no fetching and no Hibernate.isInitialized()
     * checks — it assumes everything it needs is already materialized on the DTOs
     * passed in, and is purely a calculation/reshaping step.
     *
     * "Active" purchase lots are those with remaining (unsold) quantity > 0 as of
     * the given asOf time — fully sold-out lots are excluded from the nested list,
     * though their proceeds still factor into currentMoneyAmount.
     */
    public ExperimentFinances toExperimentFinances(
            ExperimentDto experimentDto, List<WalletDto> walletDtos, Long asOf) {

        if (experimentDto == null) {
            return null;
        }

        List<ExperimentFinances.ActiveWalletBalance> walletBalances = walletDtos == null
                ? List.of()
                : walletDtos.stream()
                .map(this::toActiveWalletBalance)
                .toList();

        return new ExperimentFinances(experimentDto.id(), asOf, walletBalances);
    }

    private ExperimentFinances.ActiveWalletBalance toActiveWalletBalance(WalletDto walletDto) {
        List<PurchaseLotDto> purchaseLots = walletDto.purchaseLots() == null
                ? List.of()
                : walletDto.purchaseLots();

        BigDecimal totalPurchased = purchaseLots.stream()
                .map(this::purchaseCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaleProceeds = purchaseLots.stream()
                .flatMap(lot -> safeAssetSales(lot).stream())
                .map(this::saleProceeds)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentMoneyAmount = walletDto.startingMoneyAmount()
                .subtract(totalPurchased)
                .add(totalSaleProceeds);

        List<ExperimentFinances.ActiveWalletBalance.ActivePurchaseLot> activeLots = purchaseLots.stream()
                .map(this::toActivePurchaseLot)
                .filter(lot -> lot.currentQuantity().compareTo(BigDecimal.ZERO) > 0)
                .toList();

        return new ExperimentFinances.ActiveWalletBalance(
                walletDto.id(),
                walletDto.startingMoneyAmount(),
                currentMoneyAmount,
                walletDto.currencyCode(),
                activeLots
        );
    }

    private ExperimentFinances.ActiveWalletBalance.ActivePurchaseLot toActivePurchaseLot(PurchaseLotDto lot) {
        BigDecimal soldQuantity = safeAssetSales(lot).stream()
                .map(sale -> sale.saleQuantity() == null ? BigDecimal.ZERO : sale.saleQuantity())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentQuantity = lot.purchaseQuantity().subtract(soldQuantity);

        return new ExperimentFinances.ActiveWalletBalance.ActivePurchaseLot(
                lot.id(),
                lot.ticker(),
                lot.purchasePrice(),
                currentQuantity
        );
    }

    private BigDecimal purchaseCost(PurchaseLotDto lot) {
        return lot.purchasePrice().multiply(lot.purchaseQuantity());
    }

    private BigDecimal saleProceeds(AssetSaleDto sale) {
        return sale.salePrice().multiply(sale.saleQuantity());
    }

    private List<AssetSaleDto> safeAssetSales(PurchaseLotDto lot) {
        return lot.assetSales() == null ? List.of() : lot.assetSales();
    }
}