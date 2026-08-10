package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.db.SampleDto;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.services.SampleService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class SamplingSchedulerQueueListener {

    private final SampleService _sampleService;

    public SamplingSchedulerQueueListener(SampleService sampleService) {
        _sampleService = sampleService;
    }

    @SqsListener("sampling_scheduler_queue")
    public void listen(CreateSampleRequest message) {
        SampleDto createdSample = _sampleService.createSample(message);
        // TODO:: Improve error handling and logging
        System.out.println(createdSample);
    }
}
