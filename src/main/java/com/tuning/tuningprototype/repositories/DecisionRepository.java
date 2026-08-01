package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.Decision;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DecisionRepository extends JpaRepository<Decision, Long> {

    /**
     * Plain lookup — sample proxy, purchaseLots and assetSales collections stay uninitialized.
     */
    Optional<Decision> findById(Long id);

    /**
     * Fetches the decision with its parent sample initialized.
     */
    @EntityGraph(attributePaths = {"sample"})
    Optional<Decision> findWithSampleById(Long id);

    /**
     * Fetches the decision with its sample AND that sample's parent experiment initialized —
     * demonstrates a nested attribute path spanning two levels.
     */
    @EntityGraph(attributePaths = {"sample.experiment"})
    Optional<Decision> findWithSampleAndExperimentById(Long id);

    /**
     * Fetches the decision with its resulting purchase lots initialized.
     */
    @EntityGraph(attributePaths = {"purchaseLots"})
    Optional<Decision> findWithPurchaseLotsById(Long id);

    /**
     * Fetches the decision with its resulting asset sales initialized.
     */
    @EntityGraph(attributePaths = {"assetSales"})
    Optional<Decision> findWithAssetSalesById(Long id);

    /**
     * Fetches the decision with both purchase lots and asset sales initialized.
     */
    @EntityGraph(attributePaths = {"purchaseLots", "assetSales"})
    Optional<Decision> findWithChildrenById(Long id);

    /**
     * Fetches the decision with everything initialized.
     */
    @EntityGraph(attributePaths = {"sample", "purchaseLots", "assetSales"})
    Optional<Decision> findFullyHydratedById(Long id);

    /**
     * All decisions for a given sample, with the sample initialized.
     */
    @EntityGraph(attributePaths = {"sample"})
    List<Decision> findWithSampleBySample_Id(Long sampleId);
}