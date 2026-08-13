package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.entity.ExperimentMapper;
import com.tuning.tuningprototype.models.mappers.request.ExperimentRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class ExperimentService {

    private final ExperimentRepository _experimentRepository;
    private final ExperimentMapper _experimentMapper;
    private final ExperimentRequestMapper _experimentRequestMapper;
    private final WalletService _walletService;

    public ExperimentService(ExperimentRepository experimentRepository,
                             ExperimentMapper experimentMapper,
                             ExperimentRequestMapper experimentRequestMapper,
                             WalletService walletService) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
        _experimentRequestMapper = experimentRequestMapper;
        _walletService = walletService;
    }

    /**
     * Gets the experiment and nested data by the experiment id.
     *
     * @param id The id of the experiment
     * @param shouldDecorate Whether this method should fully hydrate the experiment with its nested data or not.
     * @return Optional of the experiment as a response DTO
     */
    public Optional<ExperimentDto> getExperiment(long id, boolean shouldDecorate) {
        if (shouldDecorate) {
            // Using wallet query, but lazy loading samples with hibernate through @BatchSize
            return _experimentRepository.findWithWalletsById(id)
                    .map(this::loadFullyHydratedExperiment)
                    .map(_experimentMapper::toDto);
        }
        return _experimentRepository.findById(id).map(_experimentMapper::toDto);
    }

    // Used for completely hydrating an experiment on gets and create
    // Expensive, do not use for bulk.
    // TODO:: Come back and review this with pagination in mind.
    private Experiment loadFullyHydratedExperiment(Experiment experiment) {
        // Allow Hibernate to load the nested data.
        experiment.getWallets()
                .forEach(wallet -> wallet.getPurchaseLots()
                        .forEach(purchaseLot -> Hibernate.initialize(purchaseLot.getAssetSales())));
        experiment.getSamples().forEach(sample -> sample.getDecisions().forEach(decision -> {
            Hibernate.initialize(decision.getAssetSales());
            Hibernate.initialize(decision.getPurchaseLots());
        }));
        return experiment;
    }

    /**
     * Creates an experiment in DRAFT state.
     *
     * @param createExperimentRequest Request DTO containing details about the experiment to create
     * @param createdUserId The id of the user that created the experiment, coming from authentication
     * @return The created experiment as a DTO response
     */
    @Transactional
    public ExperimentDto createExperiment(CreateExperimentRequest createExperimentRequest, long createdUserId) {
        Experiment createdExperiment = _experimentRepository.save(_experimentRequestMapper
                .toEntity(createExperimentRequest, createdUserId, Instant.now().getEpochSecond()));
        return _experimentMapper.toDto(createdExperiment);
    }

    /**
     * Updates an experiment while it is in DRAFT state.
     *
     * @param updateExperimentRequest Request DTO containing details to update the experiment with
     * @return The updated experiment as a DTO response
     */
    @Transactional
    public ExperimentDto updateExperiment(UpdateExperimentRequest updateExperimentRequest) {
        Experiment experimentToUpdate = _experimentRepository.getReferenceById(updateExperimentRequest.id());
        if (experimentToUpdate.getExperimentStatus() != ExperimentStatus.DRAFT) {
            throw new ExperimentException("Experiment is not in DRAFT state and is no longer editable.", true);
        }
        _experimentRequestMapper
                .applyUpdate(updateExperimentRequest, experimentToUpdate, Instant.now().getEpochSecond());
        Experiment updatedExperiment = _experimentRepository.save(experimentToUpdate);
        if (updateExperimentRequest.experimentStartTime() != null) {
            _walletService.updateStartingWalletOpenDates(
                    updateExperimentRequest.id(), updateExperimentRequest.experimentStartTime());
        }
        return _experimentMapper.toDto(updatedExperiment);
    }
}
