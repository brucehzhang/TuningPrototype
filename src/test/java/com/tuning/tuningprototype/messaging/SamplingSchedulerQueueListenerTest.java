package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.services.core.ExperimentProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SamplingSchedulerQueueListenerTest {

    @Mock
    private ExperimentProcessorService experimentProcessorService;

    private SamplingSchedulerQueueListener listener;

    @BeforeEach
    void setUp() {
        listener = new SamplingSchedulerQueueListener(experimentProcessorService);
    }

    @Test
    void listen_delegatesToStartSampling() {
        CreateSampleRequest message = new CreateSampleRequest(1L, null, 100L);

        listener.listen(message);

        verify(experimentProcessorService).startSampling(message);
    }

    @Test
    void listen_startSamplingThrows_wrapsInRuntimeException() {
        CreateSampleRequest message = new CreateSampleRequest(1L, null, 100L);
        when(experimentProcessorService.startSampling(message)).thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> listener.listen(message))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
