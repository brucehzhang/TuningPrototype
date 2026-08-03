package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.db.Wallet;
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
    Optional<Wallet> findWithPurchaseLotsById(Long id);

    /**
     * All wallets for a given experiment, via the plain FK column.
     */
    List<Wallet> findByExperimentId(Long experimentId);
}