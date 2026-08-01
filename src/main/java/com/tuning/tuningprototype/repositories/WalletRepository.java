package com.tuning.tuningprototype.repositories;

import com.tuning.tuningprototype.models.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Plain lookup — experiment proxy and purchaseLots collection stay uninitialized.
     */
    Optional<Wallet> findById(Long id);

    /**
     * Fetches the wallet with its parent experiment initialized.
     */
    @EntityGraph(attributePaths = {"experiment"})
    Optional<Wallet> findWithExperimentById(Long id);

    /**
     * Fetches the wallet with its purchase lots initialized.
     */
    @EntityGraph(attributePaths = {"purchaseLots"})
    Optional<Wallet> findWithPurchaseLotsById(Long id);

    /**
     * Fetches the wallet with both experiment and purchase lots initialized.
     */
    @EntityGraph(attributePaths = {"experiment", "purchaseLots"})
    Optional<Wallet> findFullyHydratedById(Long id);

    /**
     * All wallets for a given experiment, with the experiment initialized.
     */
    @EntityGraph(attributePaths = {"experiment"})
    List<Wallet> findWithExperimentByExperiment_Id(Long experimentId);
}