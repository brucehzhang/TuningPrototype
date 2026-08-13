package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.services.core.ExperimentProcessorService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class SamplingQueueListener {

    private final ExperimentProcessorService _experimentProcessorService;

    public SamplingQueueListener(ExperimentProcessorService experimentProcessorService) {
        _experimentProcessorService = experimentProcessorService;
    }

    @SqsListener("sampling_queue")
    public void listen(CreateSampleRequest message) {
        // TODO:: Improve error handling and logging
        System.out.println("Received message " + message);
        _experimentProcessorService.startSampling(message);
    }
}
