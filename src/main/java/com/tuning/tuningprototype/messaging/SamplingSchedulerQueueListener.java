package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.services.core.ExperimentProcessorService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SamplingSchedulerQueueListener {

    private static final Logger log = LoggerFactory.getLogger(SamplingSchedulerQueueListener.class);

    private final ExperimentProcessorService _experimentProcessorService;

    public SamplingSchedulerQueueListener(ExperimentProcessorService experimentProcessorService) {
        _experimentProcessorService = experimentProcessorService;
    }

    @SqsListener("sampling_scheduler_queue")
    public void listen(CreateSampleRequest message) {
        log.info("Received message from sampling_scheduler_queue: {}", message);
        try {
            _experimentProcessorService.startSampling(message);
        } catch (Exception e) {
            log.error("Exception occurred in while trying to start sampling from sampling_scheduler_queue {}", message);
            throw new RuntimeException(e);
        }
    }
}
