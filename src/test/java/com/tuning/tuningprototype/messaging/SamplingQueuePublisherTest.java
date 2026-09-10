package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
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
class SamplingQueuePublisherTest {

    @Mock
    private SqsTemplate sqsTemplate;
    @Mock
    private SqsSendOptions<Object> sendOptions;

    private SamplingQueuePublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new SamplingQueuePublisher(sqsTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendMessage_success_sendsToExpectedQueueWithPayload() {
        CreateSampleRequest message = new CreateSampleRequest(1L, null, 100L);

        publisher.sendMessage(message);

        ArgumentCaptor<Consumer<SqsSendOptions<Object>>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(sqsTemplate).send(captor.capture());

        when(sendOptions.queue("sampling_queue")).thenReturn(sendOptions);
        when(sendOptions.payload(message)).thenReturn(sendOptions);
        captor.getValue().accept(sendOptions);
        verify(sendOptions).queue("sampling_queue");
        verify(sendOptions).payload(message);
    }

    @Test
    void sendMessage_sqsTemplateThrows_wrapsInRuntimeException() {
        CreateSampleRequest message = new CreateSampleRequest(1L, null, 100L);
        when(sqsTemplate.send(any())).thenThrow(new RuntimeException("sqs down"));

        assertThatThrownBy(() -> publisher.sendMessage(message))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
