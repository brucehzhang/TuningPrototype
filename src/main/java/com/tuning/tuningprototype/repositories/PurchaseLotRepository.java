package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.PurchaseLot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseLotRepository extends JpaRepository<PurchaseLot, Long> {

    /**
     * Plain lookup — purchaseDecision proxy, wallet proxy, and assetSales collection
     * all stay uninitialized.
     */
    Optional<PurchaseLot> findById(Long id);

    /**
     * Fetches the purchase lot with its originating decision initialized.
     */
    @EntityGraph(attributePaths = {"purchaseDecision"})
    Optional<PurchaseLot> findWithPurchaseDecisionById(Long id);

    /**
     * Fetches the purchase lot with its funding wallet initialized.
     */
    @EntityGraph(attributePaths = {"wallet"})
    Optional<PurchaseLot> findWithWalletById(Long id);

    /**
     * Fetches the purchase lot with its asset sales initialized —
     * useful for computing realized P&L on a lot.
     */
    @EntityGraph(attributePaths = {"assetSales"})
    Optional<PurchaseLot> findWithAssetSalesById(Long id);

    /**
     * Fetches the purchase lot with everything initialized.
     */
    @EntityGraph(attributePaths = {"purchaseDecision", "wallet", "assetSales"})
    Optional<PurchaseLot> findFullyHydratedById(Long id);

    /**
     * All purchase lots for a wallet, with wallet initialized.
     */
    @EntityGraph(attributePaths = {"wallet"})
    List<PurchaseLot> findWithWalletByWallet_Id(Long walletId);

    /**
     * All purchase lots resulting from a given decision, with the decision initialized.
     */
    @EntityGraph(attributePaths = {"purchaseDecision"})
    List<PurchaseLot> findWithPurchaseDecisionByPurchaseDecision_Id(Long decisionId);
}