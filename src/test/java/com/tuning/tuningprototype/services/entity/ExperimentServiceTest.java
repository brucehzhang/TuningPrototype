package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.mappers.data.entity.ExperimentMapper;
import com.tuning.tuningprototype.models.mappers.request.ExperimentRequestMapper;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperimentServiceTest {

    @Mock
    private ExperimentRepository experimentRepository;
    @Mock
    private ExperimentMapper experimentMapper;
    @Mock
    private ExperimentRequestMapper experimentRequestMapper;
    @Mock
    private WalletService walletService;

    private ExperimentService service;

    @BeforeEach
    void setUp() {
        service = new ExperimentService(experimentRepository, experimentMapper, experimentRequestMapper, walletService);
    }

    // --- getExperiment ---

    @Test
    void getExperiment_decorated_found_usesHydratedLookup() {
        Experiment experiment = Experiment.builder().id(1L).wallets(List.of()).samples(List.of()).build();
        ExperimentDto dto = mock(ExperimentDto.class);
        when(experimentRepository.findWithWalletsById(1L)).thenReturn(Optional.of(experiment));
        when(experimentMapper.toDto(experiment)).thenReturn(dto);

        Optional<ExperimentDto> result = service.getExperiment(1L, true);

        assertThat(result).contains(dto);
        verify(experimentRepository, never()).findById(anyLong());
    }

    @Test
    void getExperiment_decorated_notFound_returnsEmpty() {
        when(experimentRepository.findWithWalletsById(1L)).thenReturn(Optional.empty());

        Optional<ExperimentDto> result = service.getExperiment(1L, true);

        assertThat(result).isEmpty();
        verifyNoInteractions(experimentMapper);
    }

    @Test
    void getExperiment_notDecorated_found_usesPlainLookup() {
        Experiment experiment = Experiment.builder().id(1L).build();
        ExperimentDto dto = mock(ExperimentDto.class);
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));
        when(experimentMapper.toDto(experiment)).thenReturn(dto);

        Optional<ExperimentDto> result = service.getExperiment(1L, false);

        assertThat(result).contains(dto);
        verify(experimentRepository, never()).findWithWalletsById(anyLong());
    }

    @Test
    void getExperiment_notDecorated_notFound_returnsEmpty() {
        when(experimentRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ExperimentDto> result = service.getExperiment(1L, false);

        assertThat(result).isEmpty();
    }

    // --- createExperiment ---

    @Test
    void createExperiment_success_savesAndReturnsDto() {
        CreateExperimentRequest request = new CreateExperimentRequest("name", null, "prompt", null, 100L, 200L);
        Experiment entityToSave = Experiment.builder().name("name").build();
        Experiment savedEntity = Experiment.builder().id(1L).name("name").build();
        ExperimentDto dto = mock(ExperimentDto.class);
        when(experimentRequestMapper.toEntity(eq(request), eq(5L), anyLong())).thenReturn(entityToSave);
        when(experimentRepository.save(entityToSave)).thenReturn(savedEntity);
        when(experimentMapper.toDto(savedEntity)).thenReturn(dto);

        ExperimentDto result = service.createExperiment(request, 5L);

        assertThat(result).isEqualTo(dto);
    }

    // --- updateExperiment ---

    @Test
    void updateExperiment_draftState_updatesAndSaves() {
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, "new name", null, null, null, 500L, null);
        Experiment existing = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.DRAFT).build();
        Experiment saved = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.DRAFT).name("new name").build();
        ExperimentDto dto = mock(ExperimentDto.class);
        when(experimentRepository.getReferenceById(1L)).thenReturn(existing);
        when(experimentRepository.save(existing)).thenReturn(saved);
        when(experimentMapper.toDto(saved)).thenReturn(dto);

        ExperimentDto result = service.updateExperiment(request);

        assertThat(result).isEqualTo(dto);
        verify(experimentRequestMapper).applyUpdate(eq(request), eq(existing), anyLong());
        verify(walletService).updateStartingWalletOpenDates(1L, 500L);
    }

    @Test
    void updateExperiment_startTimeNull_doesNotUpdateWalletOpenDates() {
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, "new name", null, null, null, null, null);
        Experiment existing = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.DRAFT).build();
        when(experimentRepository.getReferenceById(1L)).thenReturn(existing);
        when(experimentRepository.save(existing)).thenReturn(existing);
        when(experimentMapper.toDto(existing)).thenReturn(mock(ExperimentDto.class));

        service.updateExperiment(request);

        verifyNoInteractions(walletService);
    }

    @Test
    void updateExperiment_notDraftState_throwsUserExperimentException() {
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, "new name", null, null, null, null, null);
        Experiment existing = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.IN_PROGRESS).build();
        when(experimentRepository.getReferenceById(1L)).thenReturn(existing);

        assertThatThrownBy(() -> service.updateExperiment(request))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(experimentRequestMapper, walletService);
        verify(experimentRepository, never()).save(any());
    }
}
