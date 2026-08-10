package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.Wallet;
import com.tuning.tuningprototype.models.db.WalletDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.WalletMapper;
import com.tuning.tuningprototype.models.mappers.request.WalletRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateWalletRequest;
import com.tuning.tuningprototype.models.requests.UpdateWalletRequest;
import com.tuning.tuningprototype.repositories.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository _walletRepository;
    private final WalletMapper _walletMapper;
    private final WalletRequestMapper _walletRequestMapper;
    private final ExperimentStateValidator _experimentStateValidator;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper, WalletRequestMapper walletRequestMapper, ExperimentStateValidator experimentStateValidator) {
        _walletRepository = walletRepository;
        _walletMapper = walletMapper;
        _walletRequestMapper = walletRequestMapper;
        _experimentStateValidator = experimentStateValidator;
    }

    /**
     * Creates a wallet using the default values if there are no existing wallets associated.
     *
     * @param experiment The experiment that the default wallet is being created for
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createDefaultWallet(Experiment experiment) {
        List<Wallet> walletList = _walletRepository.findByExperimentId(experiment.getId());
        if (!walletList.isEmpty()) {
            System.out.println("Wallet(s) already exist, no need to create default wallet.");
            return;
        }

        Wallet wallet = Wallet.builder()
                .experimentId(experiment.getId())
                .startingMoneyAmount(BigDecimal.valueOf(100000))
                .currentMoneyAmount(BigDecimal.valueOf(100000))
                .currencyCode("USD")
                .createdTime(Instant.now().getEpochSecond())
                .modifiedTime(Instant.now().getEpochSecond())
                .build();
        _walletRepository.save(wallet);
    }

    /**
     * Creates a wallet for the starting amounts.
     * @param createWalletRequest Request DTO containing wallet creation data
     * @return The DTO for the created wallet
     */
    public WalletDto createWallet(CreateWalletRequest createWalletRequest) {
        Wallet wallet = _walletRepository.save(_walletRequestMapper.toEntity(createWalletRequest, Instant.now().getEpochSecond()));
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
}
