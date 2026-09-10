package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.messaging.SamplingAgentEventPublisher;
import com.tuning.tuningprototype.messaging.SamplingQueuePublisher;
import com.tuning.tuningprototype.messaging.SamplingScheduler;
import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;
import com.tuning.tuningprototype.models.mappers.data.entity.ExperimentMapper;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import com.tuning.tuningprototype.services.entity.SampleService;
import com.tuning.tuningprototype.services.entity.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperimentProcessorServiceTest {

    @Mock
    private ExperimentRepository experimentRepository;
    @Mock
    private ExperimentMapper experimentMapper;
    @Mock
    private FinancialSummaryService financialSummaryService;
    @Mock
    private SampleService sampleService;
    @Mock
    private SamplingQueuePublisher samplingQueuePublisher;
    @Mock
    private SamplingScheduler samplingScheduler;
    @Mock
    private SamplingAgentEventPublisher samplingAgentEventPublisher;
    @Mock
    private WalletService walletService;

    private ExperimentProcessorService service;

    @BeforeEach
    void setUp() {
        service = new ExperimentProcessorService(experimentRepository, experimentMapper, financialSummaryService,
                sampleService, samplingQueuePublisher, samplingScheduler, samplingAgentEventPublisher, walletService);
    }

    // --- runExperiment ---

    @Test
    void runExperiment_notFound_throwsUserExperimentException() {
        when(experimentRepository.findWithWalletsById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.runExperiment(1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());
    }

    @Test
    void runExperiment_notDraft_throwsUserExperimentException() {
        Experiment experiment = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.IN_PROGRESS).build();
        when(experimentRepository.findWithWalletsById(1L)).thenReturn(Optional.of(experiment));

        assertThatThrownBy(() -> service.runExperiment(1L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(walletService, samplingQueuePublisher, samplingScheduler);
    }

    @Test
    void runExperiment_startTimeInPast_publishesDirectlyToQueue() {
        long past = Instant.now().getEpochSecond() - 1000;
        Experiment experiment = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.DRAFT)
                .experimentStartTime(past).build();
        when(experimentRepository.findWithWalletsById(1L)).thenReturn(Optional.of(experiment));
        when(experimentRepository.save(experiment)).thenReturn(experiment);
        ExperimentDto dto = mock(ExperimentDto.class);
        when(experimentMapper.toDto(experiment)).thenReturn(dto);

        ExperimentDto result = service.runExperiment(1L);

        assertThat(result).isEqualTo(dto);
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.IN_PROGRESS);
        verify(walletService).createDefaultWallet(1L, past);
        verify(samplingQueuePublisher).sendMessage(any(CreateSampleRequest.class));
        verifyNoInteractions(samplingScheduler);
    }

    @Test
    void runExperiment_startTimeInFuture_schedulesSampleRun() {
        long future = Instant.now().getEpochSecond() + 100000;
        Experiment experiment = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.DRAFT)
                .experimentStartTime(future).build();
        when(experimentRepository.findWithWalletsById(1L)).thenReturn(Optional.of(experiment));
        when(experimentRepository.save(experiment)).thenReturn(experiment);
        when(experimentMapper.toDto(experiment)).thenReturn(mock(ExperimentDto.class));

        service.runExperiment(1L);

        verify(samplingScheduler).scheduleSampleRun(any(CreateSampleRequest.class));
        verifyNoInteractions(samplingQueuePublisher);
    }

    // --- startSampling ---

    private Experiment sampleExperiment() {
        return Experiment.builder().id(1L).strategyPrompt("prompt")
                .samplingWindow(SamplingWindow.HOURS_1).build();
    }

    @Test
    void startSampling_success_publishesEventAndReturnsSample() {
        CreateSampleRequest request = new CreateSampleRequest(1L, null, 500L);
        Experiment experiment = sampleExperiment();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        SampleDto createdSample = new SampleDto(10L, 1L, null, 500L, SamplingStatus.IN_PROGRESS, 1L, 1L, null);
        when(sampleService.createSample(request)).thenReturn(createdSample);
        ExperimentFinances finances = new ExperimentFinances(1L, 500L, null);
        when(financialSummaryService.getExperimentFinancesAt(1L, 500L)).thenReturn(finances);

        SampleDto result = service.startSampling(request);

        assertThat(result).isEqualTo(createdSample);
        verify(samplingAgentEventPublisher).publishSamplingAgentEvent(any());
        verify(sampleService, never()).updateSample(any(), anyLong());
    }

    @Test
    void startSampling_experimentNotFound_throws() {
        CreateSampleRequest request = new CreateSampleRequest(1L, null, 500L);
        when(experimentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.startSampling(request)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void startSampling_publishFails_marksSampleFailedButStillReturnsSample() {
        CreateSampleRequest request = new CreateSampleRequest(1L, null, 500L);
        Experiment experiment = sampleExperiment();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        SampleDto createdSample = new SampleDto(10L, 1L, null, 500L, SamplingStatus.IN_PROGRESS, 1L, 1L, null);
        when(sampleService.createSample(request)).thenReturn(createdSample);
        when(financialSummaryService.getExperimentFinancesAt(1L, 500L)).thenReturn(new ExperimentFinances(1L, 500L, null));
        doThrow(new RuntimeException("publish failed")).when(samplingAgentEventPublisher).publishSamplingAgentEvent(any());

        SampleDto result = service.startSampling(request);

        assertThat(result).isEqualTo(createdSample);
        verify(sampleService).updateSample(new UpdateSampleRequest(10L, null, SamplingStatus.FAILED), 1L);
    }

    // --- continueSampling ---

    @Test
    void continueSampling_pastEndTime_endsExperimentAndReturnsEmpty() {
        Experiment experiment = Experiment.builder().id(1L).samplingWindow(SamplingWindow.HOURS_1)
                .experimentEndTime(1000L).experimentStatus(ExperimentStatus.IN_PROGRESS).build();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        SampleDto previousSample = new SampleDto(9L, 1L, null, 900L, SamplingStatus.COMPLETED, 1L, 1L, null);
        // nextSampleTime = 900 + 3600 = 4500 > 1000 end time
        when(experimentRepository.save(experiment)).thenReturn(experiment);
        when(experimentMapper.toDto(experiment)).thenReturn(mock(ExperimentDto.class));

        Optional<SampleDto> result = service.continueSampling(1L, previousSample);

        assertThat(result).isEmpty();
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.COMPLETED);
        verifyNoInteractions(sampleService, financialSummaryService, samplingAgentEventPublisher);
    }

    @Test
    void continueSampling_beforeEndTime_createsNextSampleAndPublishes() {
        Experiment experiment = Experiment.builder().id(1L).strategyPrompt("prompt")
                .samplingWindow(SamplingWindow.HOURS_1).experimentEndTime(1_000_000L)
                .experimentStatus(ExperimentStatus.IN_PROGRESS).build();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        SampleDto previousSample = new SampleDto(9L, 1L, null, 900L, SamplingStatus.COMPLETED, 1L, 1L, null);
        SampleDto nextSample = new SampleDto(10L, 1L, null, 4500L, SamplingStatus.IN_PROGRESS, 1L, 1L, null);
        when(sampleService.createSample(any(CreateSampleRequest.class))).thenReturn(nextSample);
        when(financialSummaryService.getExperimentFinancesAt(eq(1L), anyLong()))
                .thenReturn(new ExperimentFinances(1L, 4500L, null));

        Optional<SampleDto> result = service.continueSampling(1L, previousSample);

        assertThat(result).contains(nextSample);
        verify(samplingAgentEventPublisher).publishSamplingAgentEvent(any());
        verify(experimentRepository, never()).save(any());
    }

    @Test
    void continueSampling_publishFails_marksSampleFailedButStillReturnsSample() {
        Experiment experiment = Experiment.builder().id(1L).strategyPrompt("prompt")
                .samplingWindow(SamplingWindow.HOURS_1).experimentEndTime(1_000_000L)
                .experimentStatus(ExperimentStatus.IN_PROGRESS).build();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        SampleDto previousSample = new SampleDto(9L, 1L, null, 900L, SamplingStatus.COMPLETED, 1L, 1L, null);
        SampleDto nextSample = new SampleDto(10L, 1L, null, 4500L, SamplingStatus.IN_PROGRESS, 1L, 1L, null);
        when(sampleService.createSample(any(CreateSampleRequest.class))).thenReturn(nextSample);
        when(financialSummaryService.getExperimentFinancesAt(eq(1L), anyLong()))
                .thenReturn(new ExperimentFinances(1L, 4500L, null));
        doThrow(new RuntimeException("boom")).when(samplingAgentEventPublisher).publishSamplingAgentEvent(any());

        Optional<SampleDto> result = service.continueSampling(1L, previousSample);

        assertThat(result).contains(nextSample);
        verify(sampleService).updateSample(new UpdateSampleRequest(10L, null, SamplingStatus.FAILED), 1L);
    }

    @Test
    void continueSampling_experimentNotFound_throws() {
        SampleDto previousSample = new SampleDto(9L, 1L, null, 900L, SamplingStatus.COMPLETED, 1L, 1L, null);
        when(experimentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.continueSampling(1L, previousSample)).isInstanceOf(NoSuchElementException.class);
    }

    // --- endExperiment ---

    @Test
    void endExperiment_success_setsCompletedAndSaves() {
        Experiment experiment = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.IN_PROGRESS).build();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        when(experimentRepository.save(experiment)).thenReturn(experiment);
        ExperimentDto dto = mock(ExperimentDto.class);
        when(experimentMapper.toDto(experiment)).thenReturn(dto);

        ExperimentDto result = service.endExperiment(1L);

        assertThat(result).isEqualTo(dto);
        assertThat(experiment.getExperimentStatus()).isEqualTo(ExperimentStatus.COMPLETED);
    }

    @Test
    void endExperiment_notFound_throws() {
        when(experimentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.endExperiment(1L)).isInstanceOf(NoSuchElementException.class);
    }
}
