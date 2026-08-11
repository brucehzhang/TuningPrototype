package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.messaging.SampleScheduler;
import com.tuning.tuningprototype.messaging.SamplingQueuePublisher;
import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.db.SampleDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.ExperimentMapper;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

// Processor service used for facilitation Experiment executions (start, sampling, end)
@Service
public class ExperimentProcessorService {

    private final ExperimentRepository _experimentRepository;
    private final SampleService _sampleService;
    private final WalletService _walletService;
    private final SamplingQueuePublisher _samplingQueuePublisher;
    private final SampleScheduler _sampleScheduler;
    private final ExperimentMapper _experimentMapper;

    public ExperimentProcessorService(ExperimentRepository experimentRepository,
                                      SampleService sampleService,
                                      WalletService walletService,
                                      SamplingQueuePublisher samplingQueuePublisher,
                                      SampleScheduler sampleScheduler,
                                      ExperimentMapper experimentMapper) {
        _experimentRepository = experimentRepository;
        _sampleService = sampleService;
        _walletService = walletService;
        _samplingQueuePublisher = samplingQueuePublisher;
        _sampleScheduler = sampleScheduler;
        _experimentMapper = experimentMapper;
    }

    /**
     * Starts the experiment by determining if this experiment starts in the past or future.
     * If past sampling, directly samples through message broker.
     * If future sampling, sets up the initial one-off scheduled job dated at the future start date.
     *
     * @param experimentId Id of the experiment
     * @return The dto of the started experiment
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

    /**
     * Creates a sample in the DB and send message for agent consumers to begin decision-making.
     *
     * @param createSampleRequest The request dto for creating the sample
     * @return The dto of the created sample
     */
    public SampleDto startSampling(CreateSampleRequest createSampleRequest) {
        SampleDto createdSample = _sampleService.createSample(createSampleRequest);
        // TODO:: Send AWS Kinesis message for agents to begin decision-making
        System.out.println("Agent call happening for sample " + createdSample);
        return createdSample;
    }

    /**
     * After the final sample is completed, end this experiment by marking it as COMPLETED and perform other actions
     * such as informing the user, generating reports, etc.
     *
     * @param experimentId The id of the experiment that is ending
     * @return The dto of the experiment that has ended.
     */
    public ExperimentDto endExperiment(long experimentId) {
        // TODO:: Implement this
        System.out.println("NO-OP");
        return null;
    }
}
