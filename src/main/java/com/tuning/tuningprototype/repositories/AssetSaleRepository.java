package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.AssetSale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetSaleRepository extends JpaRepository<AssetSale, Long> {

    /**
     * Plain lookup — saleDecision and purchaseLot proxies stay uninitialized.
     * AssetSale has no @OneToMany children (leaf node in the graph).
     */
    Optional<AssetSale> findById(Long id);

    /**
     * Fetches the asset sale with its sale decision initialized.
     */
    @EntityGraph(attributePaths = {"saleDecision"})
    Optional<AssetSale> findWithSaleDecisionById(Long id);

    /**
     * Fetches the asset sale with its purchase lot initialized.
     */
    @EntityGraph(attributePaths = {"purchaseLot"})
    Optional<AssetSale> findWfindWithPurchaseLotByIdfindWithPurchaseLotById(Long id);

    /**
     * Fetches the asset sale with both associations initialized.
     */
    @EntityGraph(attributePaths = {"saleDecision", "purchaseLot"})
    Optional<AssetSale> findFullyHydratedById(Long id);

    /**
     * All asset sales for a given purchase lot, with the lot initialized —
     * useful for computing how much of a lot has been sold off.
     */
    @EntityGraph(attributePaths = {"purchaseLot"})
    List<AssetSale> findWithPurchaseLotByPurchaseLot_Id(Long purchaseLotId);

    /**
     * All asset sales resulting from a given decision, with the decision initialized.
     */
    @EntityGraph(attributePaths = {"saleDecision"})
    List<AssetSale> findWithSaleDecisionBySaleDecision_Id(Long decisionId);
}