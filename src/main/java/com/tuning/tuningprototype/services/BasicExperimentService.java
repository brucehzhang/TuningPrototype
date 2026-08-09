package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.db.Sample;
import com.tuning.tuningprototype.models.db.SampleDto;
import com.tuning.tuningprototype.models.mappers.data.ExperimentMapper;
import com.tuning.tuningprototype.models.mappers.request.ExperimentRequestMapper;
import com.tuning.tuningprototype.models.mappers.data.SampleMapper;
import com.tuning.tuningprototype.models.mappers.request.SampleRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import com.tuning.tuningprototype.repositories.SampleRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service("basicExperimentService")
public class BasicExperimentService implements IExperimentService {

    private final ExperimentRepository _experimentRepository;
    private final ExperimentMapper _experimentMapper;
    private final ExperimentRequestMapper _experimentRequestMapper;
    private final SampleRepository _sampleRepository;
    private final SampleMapper _sampleMapper;
    private final SampleRequestMapper _sampleRequestMapper;

    public BasicExperimentService(ExperimentRepository experimentRepository,
                                  ExperimentMapper experimentMapper,
                                  ExperimentRequestMapper experimentRequestMapper,
                                  SampleRepository sampleRepository,
                                  SampleMapper sampleMapper,
                                  SampleRequestMapper sampleRequestMapper) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
        _experimentRequestMapper = experimentRequestMapper;
        _sampleRepository = sampleRepository;
        _sampleMapper = sampleMapper;
        _sampleRequestMapper = sampleRequestMapper;
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

    /**
     * Updates an experiment while it is in DRAFT state.
     *
     * @param updateExperimentRequest Request DTO containing details to update the experiment with
     * @return The updated experiment as a DTO response
     */
    @Override
    public ExperimentDto updateExperiment(UpdateExperimentRequest updateExperimentRequest) {
        Experiment experimentToUpdate = _experimentRepository.getReferenceById(updateExperimentRequest.id());
        _experimentRequestMapper
                .applyUpdate(updateExperimentRequest, experimentToUpdate, Instant.now().getEpochSecond());
        Experiment updatedExperiment = _experimentRepository.save(experimentToUpdate);
        return _experimentMapper.toDto(updatedExperiment);
    }

    /**
     * Creates a sample in IN_PROGRESS state.
     *
     * @param createSampleRequest Request DTO containing details about the sample to create
     * @return The created sample as a DTO response
     */
    @Override
    public SampleDto createSample(CreateSampleRequest createSampleRequest) {
        Sample createdSample = _sampleRepository.save(_sampleRequestMapper
                .toEntity(createSampleRequest, Instant.now().getEpochSecond()));
        return _sampleMapper.toDto(createdSample);
    }

    /**
     * Updates a sample to update market insights and toggle sampling state.
     *
     * @param updateSampleRequest Request DTO containing details to update the sample with
     * @param experimentId The id of the experiment, used to verify that sample being updated is for the same experiment.
     * @return The updated sample as a DTO response
     */
    @Override
    public SampleDto updateSample(UpdateSampleRequest updateSampleRequest, long experimentId) {
        Sample sampleToUpdate = _sampleRepository.getReferenceById(updateSampleRequest.id());
        if (sampleToUpdate.getExperimentId() != experimentId) {
            throw new ExperimentException("Attempting to update sample that is not part of the requested experiment.");
        }
        _sampleRequestMapper
                .applyUpdate(updateSampleRequest, sampleToUpdate, Instant.now().getEpochSecond());
        Sample updatedSample = _sampleRepository.save(sampleToUpdate);
        return _sampleMapper.toDto(updatedSample);
    }
}
