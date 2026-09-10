package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.events.SamplingAgentEvent;
import io.awspring.cloud.sqs.operations.SqsSendOptions;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SamplingAgentEventPublisherTest {

    @Mock
    private SqsTemplate sqsTemplate;
    @Mock
    private SqsSendOptions<Object> sendOptions;

    private SamplingAgentEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new SamplingAgentEventPublisher(sqsTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishSamplingAgentEvent_success_sendsToExpectedQueueWithPayload() {
        SamplingAgentEvent event = new SamplingAgentEvent(1L, "prompt", null, null, 10L, 100L, null);

        publisher.publishSamplingAgentEvent(event);

        ArgumentCaptor<Consumer<SqsSendOptions<Object>>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(sqsTemplate).send(captor.capture());

        when(sendOptions.queue("sampling_agent_event_queue")).thenReturn(sendOptions);
        when(sendOptions.payload(event)).thenReturn(sendOptions);
        captor.getValue().accept(sendOptions);
        verify(sendOptions).queue("sampling_agent_event_queue");
        verify(sendOptions).payload(event);
    }

    @Test
    void publishSamplingAgentEvent_sqsTemplateThrows_wrapsInRuntimeException() {
        SamplingAgentEvent event = new SamplingAgentEvent(1L, "prompt", null, null, 10L, 100L, null);
        when(sqsTemplate.send(any())).thenThrow(new RuntimeException("sqs down"));

        assertThatThrownBy(() -> publisher.publishSamplingAgentEvent(event))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
