package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.mappers.ExperimentMapper;
import com.tuning.tuningprototype.models.mappers.ExperimentRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service("basicExperimentService")
public class BasicExperimentService implements IExperimentService {

    private final ExperimentRepository _experimentRepository;
    private final ExperimentMapper _experimentMapper;
    private final ExperimentRequestMapper _experimentRequestMapper;

    public BasicExperimentService(ExperimentRepository experimentRepository, ExperimentMapper experimentMapper, ExperimentRequestMapper experimentRequestMapper) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
        _experimentRequestMapper = experimentRequestMapper;
    }

    /**
     * Gets the experiment and nested data by the experiment id.
     * @param id The id of the experiment
     * @return Optional of the experiment as a response DTO
     */
    @Override
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
    @Override
    public ExperimentDto createExperiment(CreateExperimentRequest createExperimentRequest, long createdUserId) {
        Experiment createdExperiment = _experimentRepository.save(_experimentRequestMapper
                .toEntity(createExperimentRequest, createdUserId, Instant.now().getEpochSecond()));
        return _experimentMapper.toDto(createdExperiment);
    }
}
