package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.entity.PurchaseLot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PurchaseLotRepository extends JpaRepository<PurchaseLot, Long> {

    /**
     * Plain lookup — assetSales collection stays uninitialized.
     * No parent associations to fetch since @ManyToOne to Decision/Wallet were removed.
     */
    Optional<PurchaseLot> findById(Long id);

    /**
     * Fetches the purchase lot with its asset sales initialized in one query —
     * useful for computing realized P&L on a lot.
     */
    @EntityGraph(attributePaths = {"assetSales"})
    Optional<PurchaseLot> findWithAssetSalesById(Long id);

    /**
     * All purchase lots for a given wallet, via the plain FK column.
     */
    List<PurchaseLot> findByWalletId(Long walletId);

    /**
     * Non-nested lookup for purchase lots by wallet ids and purchase time less than equal
     */
    List<PurchaseLot> findByWalletIdInAndPurchaseTimeIsLessThanEqual(Collection<Long> walletIds, Long purchaseTimeIsLessThan);

    /**
     * All purchase lots resulting from a given decision, via the plain FK column.
     */
    List<PurchaseLot> findByPurchaseDecisionId(Long decisionId);

    List<PurchaseLot> findByWalletIdInAndTicker(Collection<Long> walletIds, String ticker);
}