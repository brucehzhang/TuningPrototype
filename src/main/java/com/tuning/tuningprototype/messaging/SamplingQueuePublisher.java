package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SamplingQueuePublisher {

    private static final Logger log = LoggerFactory.getLogger(SamplingQueuePublisher.class);
    private static final String QUEUE_NAME = "sampling_queue";
    private final SqsTemplate _sqsTemplate;

    public SamplingQueuePublisher(SqsTemplate sqsTemplate) {
        _sqsTemplate = sqsTemplate;
    }

    public void sendMessage(CreateSampleRequest message) {
        try {
            _sqsTemplate.send(to -> to.queue(QUEUE_NAME).payload(message));
        } catch (Exception e) {
            log.error("Exception occurred while sending event to queue {}: {}", QUEUE_NAME, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
