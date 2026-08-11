package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.AssetSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AssetSaleRepository extends JpaRepository<AssetSale, Long> {

    /**
     * Plain lookup — AssetSale has no children and no parent associations
     * (both were plain FKs to begin with), so no @EntityGraph variants needed.
     */
    Optional<AssetSale> findById(Long id);

    /**
     * All asset sales for a given purchase lot, via the plain FK column —
     * useful for computing how much of a lot has been sold off.
     */
    List<AssetSale> findByPurchaseLotId(Long purchaseLotId);

    /**
     * Non-nested lookup for asset sales by purchase lot ids and sale time less than equal
     */
    List<AssetSale> findByPurchaseLotIdInAndSaleTimeIsLessThanEqual(Collection<Long> purchaseLotIds, Long saleTimeIsLessThan);

    /**
     * All asset sales resulting from a given decision, via the plain FK column.
     */
    List<AssetSale> findBySaleDecisionId(Long decisionId);
}