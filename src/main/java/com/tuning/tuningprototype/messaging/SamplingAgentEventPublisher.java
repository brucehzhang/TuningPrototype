package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.events.SamplingAgentEvent;
import io.awspring.cloud.sqs.operations.SqsTemplate;
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

    private static final String QUEUE_NAME = "sampling_agent_event_queue";
    private final SqsTemplate _sqsTemplate;

    public SamplingAgentEventPublisher(SqsTemplate sqsTemplate) {
        _sqsTemplate = sqsTemplate;
    }

    // Temporarily use SQS as part of prototype, Kinesis is not on free-tier.
    public boolean publishSamplingAgentEvent(SamplingAgentEvent event) {
        _sqsTemplate.send(to -> to.queue(QUEUE_NAME).payload(event));
        return true;
    }
}
