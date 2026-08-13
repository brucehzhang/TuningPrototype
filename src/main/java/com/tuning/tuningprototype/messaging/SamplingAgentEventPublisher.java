package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.events.SamplingAgentEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class SamplingAgentEventPublisher {

    private static final String TARGET_BINDING = "publishSamplingAgentEvent-out-0";
    private final StreamBridge _streamBridge;

    public SamplingAgentEventPublisher(StreamBridge streamBridge) {
        _streamBridge = streamBridge;
    }

    public boolean publishSamplingAgentEvent(SamplingAgentEvent event) {
        return _streamBridge.send(TARGET_BINDING, event);
    }
}
