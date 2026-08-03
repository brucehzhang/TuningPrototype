package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.Decision;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DecisionRepository extends JpaRepository<Decision, Long> {

    /**
     * Plain lookup — purchaseLots and assetSales collections stay uninitialized.
     * No parent association to fetch since @ManyToOne to Sample was removed.
     */
    Optional<Decision> findById(Long id);

    /**
     * Fetches the decision with its resulting purchase lots initialized in one query.
     */
    @EntityGraph(attributePaths = {"purchaseLots"})
    Optional<Decision> findWithPurchaseLotsById(Long id);

    /**
     * Fetches the decision with its resulting asset sales initialized in one query.
     */
    @EntityGraph(attributePaths = {"assetSales"})
    Optional<Decision> findWithAssetSalesById(Long id);

    /**
     * Fetches the decision with purchase lots eagerly joined. assetSales is
     * intentionally excluded — both are List (bag) collections, and fetch-joining
     * two sibling bags in one query throws MultipleBagFetchException. Rely on
     * @BatchSize on Decision.assetSales for efficient lazy access if touched afterward.
     */
    @EntityGraph(attributePaths = {"purchaseLots"})
    Optional<Decision> findFullyHydratedById(Long id);

    /**
     * All decisions for a given sample, via the plain FK column.
     */
    List<Decision> findBySampleId(Long sampleId);
}