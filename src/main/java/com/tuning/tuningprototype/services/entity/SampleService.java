package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.mappers.data.entity.SampleMapper;
import com.tuning.tuningprototype.models.mappers.request.SampleRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import com.tuning.tuningprototype.repositories.SampleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class SampleService {

    private final SampleRepository _sampleRepository;
    private final SampleMapper _sampleMapper;
    private final SampleRequestMapper _sampleRequestMapper;

    public SampleService(SampleRepository sampleRepository, SampleMapper sampleMapper, SampleRequestMapper sampleRequestMapper) {
        _sampleRepository = sampleRepository;
        _sampleMapper = sampleMapper;
        _sampleRequestMapper = sampleRequestMapper;
    }

    /**
     * Creates a sample in IN_PROGRESS state.
     *
     * @param createSampleRequest Request DTO containing details about the sample to create
     * @return The created sample as a DTO response
     */
    @Transactional(propagation = Propagation.SUPPORTS)
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
    @Transactional(propagation = Propagation.SUPPORTS)
    public SampleDto updateSample(UpdateSampleRequest updateSampleRequest, long experimentId) {
        Sample sampleToUpdate = _sampleRepository.getReferenceById(updateSampleRequest.id());
        if (sampleToUpdate.getExperimentId() != experimentId) {
            throw new ExperimentException("Attempting to update sample that is not part of the requested experiment.", true);
        }
        _sampleRequestMapper
                .applyUpdate(updateSampleRequest, sampleToUpdate, Instant.now().getEpochSecond());
        Sample updatedSample = _sampleRepository.save(sampleToUpdate);
        return _sampleMapper.toDto(updatedSample);
    }
}
