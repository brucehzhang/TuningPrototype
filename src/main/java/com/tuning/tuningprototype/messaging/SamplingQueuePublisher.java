package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SamplingQueuePublisher {

    private static final String QUEUE_NAME = "sampling_queue";
    private final SqsTemplate _sqsTemplate;

    public SamplingQueuePublisher(SqsTemplate sqsTemplate) {
        _sqsTemplate = sqsTemplate;
    }

    public void sendMessage(CreateSampleRequest message) {
        _sqsTemplate.send(to -> to.queue(QUEUE_NAME).payload(message));
    }
}
