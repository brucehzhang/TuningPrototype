package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.events.SamplingAgentEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SamplingAgentEventPublisher {
//
//    private static final String TARGET_BINDING = "publishSamplingAgentEvent-out-0";
//    private final StreamBridge _streamBridge;
//
//    public SamplingAgentEventPublisher(StreamBridge streamBridge) {
//        _streamBridge = streamBridge;
//    }
//
//    public boolean publishSamplingAgentEvent(SamplingAgentEvent event) {
//        return _streamBridge.send(TARGET_BINDING, event);
//    }

    private static final Logger log = LoggerFactory.getLogger(SamplingAgentEventPublisher.class);
    private static final String QUEUE_NAME = "sampling_agent_event_queue";
    private final SqsTemplate _sqsTemplate;

    public SamplingAgentEventPublisher(SqsTemplate sqsTemplate) {
        _sqsTemplate = sqsTemplate;
    }

    // Temporarily use SQS as part of prototype, Kinesis is not on free-tier.
    public void publishSamplingAgentEvent(SamplingAgentEvent event) {
        try {
            _sqsTemplate.send(to -> to.queue(QUEUE_NAME).payload(event));
        } catch (Exception e) {
            log.error("Exception occurred while sending event to queue {}: {}", QUEUE_NAME, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
