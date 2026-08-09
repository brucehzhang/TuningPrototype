package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.mappers.data.ExperimentMapper;
import com.tuning.tuningprototype.models.mappers.request.ExperimentRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ExperimentService {

    private final ExperimentRepository _experimentRepository;
    private final ExperimentMapper _experimentMapper;
    private final ExperimentRequestMapper _experimentRequestMapper;

    public ExperimentService(ExperimentRepository experimentRepository,
                             ExperimentMapper experimentMapper,
                             ExperimentRequestMapper experimentRequestMapper) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
        _experimentRequestMapper = experimentRequestMapper;
    }

    /**
     * Gets the experiment and nested data by the experiment id.
     * @param id The id of the experiment
     * @return Optional of the experiment as a response DTO
     */
    public Optional<ExperimentDto> getExperiment(long id) {
        // Using wallet query, but lazy loading samples with hibernate through @BatchSize
        return _experimentRepository.findWithWalletsById(id)
                .map(this::loadFullyHydratedExperiment)
                .map(_experimentMapper::toDto);
    }

    // Used for completely hydrating an experiment
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
    public ExperimentDto updateExperiment(UpdateExperimentRequest updateExperimentRequest) {
        Experiment experimentToUpdate = _experimentRepository.getReferenceById(updateExperimentRequest.id());
        _experimentRequestMapper
                .applyUpdate(updateExperimentRequest, experimentToUpdate, Instant.now().getEpochSecond());
        Experiment updatedExperiment = _experimentRepository.save(experimentToUpdate);
        return _experimentMapper.toDto(updatedExperiment);
    }

    /**
     * Starts the experiment by creating the default wallet if non exist, then determining if this experiment starts with
     * past or future dated sampling by the experiment start date. If past sampling, directly samples through message broker.
     * If future sampling, sets up initial CRON job dated for the first future sampling.
     *
     * @param experimentId Id of the experiment
     */
    public void startExperiment(long experimentId) {
        // TODO:: Implement this.
    }
}
