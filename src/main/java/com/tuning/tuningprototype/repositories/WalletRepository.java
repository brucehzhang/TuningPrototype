package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.entity.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Plain lookup — purchaseLots collection stays uninitialized.
     * No parent association to fetch since @ManyToOne to Experiment was removed.
     */
    Optional<Wallet> findById(Long id);

    /**
     * Fetches the wallet with its purchase lots initialized in one query.
     */
    @EntityGraph(attributePaths = {"purchaseLots"})
    Optional<Wallet> findWithPurchaseLotsByExperimentId(Long id);

    /**
     * All wallets for a given experiment, via the plain FK column.
     */
    List<Wallet> findByExperimentId(Long experimentId);

    /**
     * Non-nested lookup for wallet by experiment id and opened time less than equal
     */
    List<Wallet> findByExperimentIdAndOpenedTimeIsLessThanEqual(Long experimentId, Long openedTimeIsLessThan);
}