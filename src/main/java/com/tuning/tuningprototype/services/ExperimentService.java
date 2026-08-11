package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.messaging.SampleScheduler;
import com.tuning.tuningprototype.messaging.SamplingQueuePublisher;
import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.ExperimentMapper;
import com.tuning.tuningprototype.models.mappers.request.ExperimentRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
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
    private final SamplingQueuePublisher _samplingQueuePublisher;
    private final SampleScheduler _sampleScheduler;
    private final WalletService _walletService;

    public ExperimentService(ExperimentRepository experimentRepository,
                             ExperimentMapper experimentMapper,
                             ExperimentRequestMapper experimentRequestMapper,
                             SamplingQueuePublisher samplingQueuePublisher,
                             SampleScheduler sampleScheduler, WalletService walletService) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
        _experimentRequestMapper = experimentRequestMapper;
        _samplingQueuePublisher = samplingQueuePublisher;
        _sampleScheduler = sampleScheduler;
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

    /**
     * Starts the experiment by determining if this experiment starts in the past or future.
     * If past sampling, directly samples through message broker.
     * If future sampling, sets up the initial one-off scheduled job dated at the future start date.
     *
     * @param experimentId Id of the experiment
     */
    @Transactional
    public ExperimentDto runExperiment(long experimentId) {
        Experiment experiment = _experimentRepository.findWithWalletsById(experimentId)
                .orElseThrow(() -> new ExperimentException("Experiment " + experimentId + " could not be found.", true));
        if (experiment.getExperimentStatus() != ExperimentStatus.DRAFT) {
            throw new ExperimentException("Experiment " + experimentId + " is not in DRAFT state and cannot be started.", true);
        }
        // Creates the default wallet with 100000 starting amount and currency if it doesn't exist.
        _walletService.createDefaultWallet(experimentId, experiment.getExperimentStartTime());
        // Update experiment to IN_PROGRESS
        experiment.setExperimentStatus(ExperimentStatus.IN_PROGRESS);
        Experiment updatedExperiment = _experimentRepository.save(experiment);
        if (experiment.getExperimentStartTime() < Instant.now().getEpochSecond()) {
            _samplingQueuePublisher.sendMessage(new CreateSampleRequest(experiment.getId(), null, experiment.getExperimentStartTime()));
        } else {
            _sampleScheduler.scheduleSampleRun(new CreateSampleRequest(experiment.getId(), null, experiment.getExperimentStartTime()));
        }
        return _experimentMapper.toDto(updatedExperiment);
    }
}
