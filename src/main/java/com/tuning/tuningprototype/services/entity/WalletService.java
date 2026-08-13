package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.*;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.entity.WalletMapper;
import com.tuning.tuningprototype.models.mappers.request.WalletRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateWalletRequest;
import com.tuning.tuningprototype.models.requests.UpdateWalletRequest;
import com.tuning.tuningprototype.repositories.AssetSaleRepository;
import com.tuning.tuningprototype.repositories.PurchaseLotRepository;
import com.tuning.tuningprototype.repositories.WalletRepository;
import com.tuning.tuningprototype.services.core.ExperimentStateValidator;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final WalletRepository _walletRepository;
    private final WalletMapper _walletMapper;
    private final WalletRequestMapper _walletRequestMapper;
    private final ExperimentStateValidator _experimentStateValidator;
    private final PurchaseLotRepository _purchaseLotRepository;
    private final AssetSaleRepository _assetSaleRepository;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper, WalletRequestMapper walletRequestMapper, ExperimentStateValidator experimentStateValidator, PurchaseLotRepository purchaseLotRepository, AssetSaleRepository assetSaleRepository) {
        _walletRepository = walletRepository;
        _walletMapper = walletMapper;
        _walletRequestMapper = walletRequestMapper;
        _experimentStateValidator = experimentStateValidator;
        _purchaseLotRepository = purchaseLotRepository;
        _assetSaleRepository = assetSaleRepository;
    }

    /**
     * Gets the wallets and associated purchases/sales/etc by the experiment and point in time.
     *
     * @param experimentId Id of the experiment to fetch wallets from
     * @param checkTime Point in time used to fetch wallet and associated purchases and sales
     * @return List of Wallet DTOs with the associated purchases and sales at the point in time.
     */
    @Transactional(readOnly = true)
    public List<WalletDto> getWalletsByExperiment(long experimentId, Long checkTime) {
        if (checkTime == null) {
            // Use Hibernate and hydrate purchase lots.
            return _walletRepository.findWithPurchaseLotsByExperimentId(experimentId).stream()
                    .map(this::loadFullyHydratedWallet)
                    .map(_walletMapper::toDto)
                    .toList();
        }

        return loadPointInTimeWallets(experimentId, checkTime).stream()
                .map(_walletMapper::toDto)
                .toList();
    }

    // Used for completely hydrating a wallet with ALL purchase and sales details, does not consider any point in time
    private Wallet loadFullyHydratedWallet(Wallet wallet) {
        // Allow Hibernate to load the nested data.
        wallet.getPurchaseLots().forEach(purchaseLot -> Hibernate.initialize(purchaseLot.getAssetSales()));
        return wallet;
    }

    // Used for only partially loading wallets based on the provided check time.
    private List<Wallet> loadPointInTimeWallets(Long experimentId, Long checkTime) {
        List<Wallet> wallets = _walletRepository
                .findByExperimentIdAndOpenedTimeIsLessThanEqual(experimentId, checkTime);
        if (wallets.isEmpty()) {
            return wallets;
        }
        List<Long> walletIds = wallets.stream().map(Wallet::getId).toList();

        List<PurchaseLot> purchaseLots = _purchaseLotRepository
                .findByWalletIdInAndPurchaseTimeIsLessThanEqual(walletIds, checkTime);
        List<Long> purchaseLotIds = purchaseLots.stream().map(PurchaseLot::getId).toList();
        List<AssetSale> assetSales = purchaseLotIds.isEmpty()
                ? List.of()
                : _assetSaleRepository.findByPurchaseLotIdInAndSaleTimeIsLessThanEqual(purchaseLotIds, checkTime);

        // Group children by parent id for O(1) lookup while nesting below
        Map<Long, List<AssetSale>> salesByPurchaseLotId = assetSales.stream()
                .collect(Collectors.groupingBy(AssetSale::getPurchaseLotId));
        Map<Long, List<PurchaseLot>> lotsByWalletId = purchaseLots.stream()
                .collect(Collectors.groupingBy(PurchaseLot::getWalletId));

        // Nest asset sales into each purchase lot
        purchaseLots.forEach(lot ->
                lot.setAssetSales(salesByPurchaseLotId.getOrDefault(lot.getId(), List.of())));
        // Nest purchase lots into each wallet
        wallets.forEach(wallet ->
                wallet.setPurchaseLots(lotsByWalletId.getOrDefault(wallet.getId(), List.of())));
        return wallets;
    }

    /**
     * Creates a wallet using the default values if there are no existing wallets associated.
     *
     * @param id The experiment that the default wallet is being created for
     * @param openedTime The openedTime to be set for the default wallet, should be the experiment start time.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createDefaultWallet(long id, long openedTime) {
        List<Wallet> walletList = _walletRepository.findByExperimentId(id);
        if (!walletList.isEmpty()) {
            System.out.println("Wallet(s) already exist, no need to create default wallet.");
            return;
        }

        Wallet wallet = Wallet.builder()
                .experimentId(id)
                .startingMoneyAmount(BigDecimal.valueOf(100000))
                .openedTime(openedTime)
                .currencyCode("USD")
                .createdTime(Instant.now().getEpochSecond())
                .modifiedTime(Instant.now().getEpochSecond())
                .build();
        _walletRepository.save(wallet);
    }

    /**
     * Creates a wallet for the starting amounts.
     *
     * @param createWalletRequest Request DTO containing wallet creation data
     * @param experimentDto The DTO of the experiment, check state and ensure the right openedTime is set on the entity
     * @return The DTO for the created wallet
     */
    public WalletDto createWallet(CreateWalletRequest createWalletRequest, ExperimentDto experimentDto) {
        Wallet walletToSave = _walletRequestMapper.toEntity(createWalletRequest, Instant.now().getEpochSecond());
        if (experimentDto.experimentStatus() == ExperimentStatus.DRAFT) {
            walletToSave.setOpenedTime(experimentDto.experimentStartTime());
        }
        Wallet wallet = _walletRepository.save(walletToSave);
        return _walletMapper.toDto(wallet);
    }

    /**
     * Updates a wallet's starting amounts and currency while the experiment is in DRAFT state.
     *
     * @param updateWalletRequest Request DTO containing details to update the sample with
     * @param experimentId The id of the experiment, used to verify that sample being updated is for the same experiment.
     * @return The updated sample as a DTO response
     */
    public WalletDto updateStartingWallet(UpdateWalletRequest updateWalletRequest, long experimentId) {
        Wallet walletToUpdate = _walletRepository.getReferenceById(updateWalletRequest.id());
        if (walletToUpdate.getExperimentId() != experimentId) {
            throw new ExperimentException("Attempting to update wallet that is not part of the requested experiment.", true);
        }
        if (ExperimentStatus.DRAFT != _experimentStateValidator.getStatus(experimentId)) {
            throw new ExperimentException("Experiment is not in DRAFT state, the starting wallet amount/currency cannot be updated.", true);
        }
        _walletRequestMapper
                .applyUpdate(updateWalletRequest, walletToUpdate, Instant.now().getEpochSecond());
        Wallet updatedWallet = _walletRepository.save(walletToUpdate);
        return _walletMapper.toDto(updatedWallet);
    }

    /**
     * Update the starting wallet with new open dates. Used when the experiment's start time is updated.
     *
     * @param experimentId The id of the experiment
     * @param openedTime The openedTime to update the starting wallets with, should be the experiment's start time
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateStartingWalletOpenDates(long experimentId, long openedTime) {
        List<Wallet> wallets = _walletRepository.findByExperimentId(experimentId);
        if (wallets != null) {
            wallets.forEach(wallet -> wallet.setOpenedTime(openedTime));
            _walletRepository.saveAll(wallets);
        }
    }
}
