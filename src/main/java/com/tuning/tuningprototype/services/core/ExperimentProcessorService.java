package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.messaging.SamplingAgentEventPublisher;
import com.tuning.tuningprototype.messaging.SamplingQueuePublisher;
import com.tuning.tuningprototype.messaging.SamplingScheduler;
import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.models.events.SamplingAgentEvent;
import com.tuning.tuningprototype.models.mappers.data.entity.ExperimentMapper;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import com.tuning.tuningprototype.services.entity.SampleService;
import com.tuning.tuningprototype.services.entity.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

// Processor service used for facilitation Experiment executions (start, sampling, end)
@Service
public class ExperimentProcessorService {

    private static final Logger log = LoggerFactory.getLogger(ExperimentProcessorService.class);

    private final ExperimentRepository _experimentRepository;
    private final ExperimentMapper _experimentMapper;
    private final FinancialSummaryService _financialSummaryService;
    private final SampleService _sampleService;
    private final SamplingQueuePublisher _samplingQueuePublisher;
    private final SamplingScheduler _samplingScheduler;
    private final SamplingAgentEventPublisher _samplingAgentEventPublisher;
    private final WalletService _walletService;

    public ExperimentProcessorService(ExperimentRepository experimentRepository,
                                      ExperimentMapper experimentMapper,
                                      FinancialSummaryService financialSummaryService,
                                      SampleService sampleService,
                                      SamplingQueuePublisher samplingQueuePublisher,
                                      SamplingScheduler samplingScheduler, SamplingAgentEventPublisher samplingAgentEventPublisher,
                                      WalletService walletService) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
        _financialSummaryService = financialSummaryService;
        _sampleService = sampleService;
        _samplingQueuePublisher = samplingQueuePublisher;
        _samplingScheduler = samplingScheduler;
        _samplingAgentEventPublisher = samplingAgentEventPublisher;
        _walletService = walletService;
    }

    /**
     * Starts the experiment by determining if this experiment starts in the past or future.
     * If past sampling, directly samples through message broker.
     * If future sampling, sets up the initial one-off scheduled job dated at the future start date.
     *
     * @param experimentId Id of the experiment
     * @return The dto of the started experiment
     */
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
            log.info("Directly starting sampling for experiment {}", experimentId);
            _samplingQueuePublisher.sendMessage(new CreateSampleRequest(experiment.getId(), null, experiment.getExperimentStartTime()));
        } else {
            log.info("Scheduling first sampling for experiment {} at {}", experimentId, experiment.getExperimentStartTime());
            _samplingScheduler.scheduleSampleRun(new CreateSampleRequest(experiment.getId(), null, experiment.getExperimentStartTime()));
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
        Experiment experiment = _experimentRepository.findById(createSampleRequest.experimentId())
                .orElseThrow();
        SampleDto createdSample = _sampleService.createSample(createSampleRequest);
        ExperimentFinances finances = _financialSummaryService.getExperimentFinancesAt(createdSample.experimentId(), createSampleRequest.samplingTime());
        SamplingAgentEvent event = new SamplingAgentEvent(experiment.getId(),
                experiment.getStrategyPrompt(),
                experiment.getAgentModel(),
                experiment.getSamplingWindow(),
                createdSample.id(),
                createdSample.samplingTime(),
                finances);
        try {
            log.info("Publishing sampling agent event: {}", event);
            _samplingAgentEventPublisher.publishSamplingAgentEvent(event);
            log.info("Sampling agent event delivered for sample {}", createdSample.id());
        } catch (Exception e) {
            log.error("Sampling agent event could not be delivered for sample {}", createdSample.id());
            _sampleService.updateSample(new UpdateSampleRequest(createdSample.id(), null, SamplingStatus.FAILED), experiment.getId());
        }
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
        log.warn("NO-OP");
        return null;
    }
}
