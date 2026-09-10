package com.tuning.tuningprototype.services.entity;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.models.mappers.data.entity.SampleMapper;
import com.tuning.tuningprototype.models.mappers.request.SampleRequestMapper;
import com.tuning.tuningprototype.repositories.SampleRepository;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleRepository sampleRepository;
    @Mock
    private SampleMapper sampleMapper;
    @Mock
    private SampleRequestMapper sampleRequestMapper;

    private SampleService service;

    @BeforeEach
    void setUp() {
        service = new SampleService(sampleRepository, sampleMapper, sampleRequestMapper);
    }

    @Test
    void createSample_success_savesAndReturnsDto() {
        CreateSampleRequest request = new CreateSampleRequest(1L, "insights", 100L);
        Sample entityToSave = Sample.builder().experimentId(1L).build();
        Sample savedEntity = Sample.builder().id(10L).experimentId(1L).build();
        SampleDto dto = mock(SampleDto.class);
        when(sampleRequestMapper.toEntity(eq(request), anyLong())).thenReturn(entityToSave);
        when(sampleRepository.save(entityToSave)).thenReturn(savedEntity);
        when(sampleMapper.toDto(savedEntity)).thenReturn(dto);

        SampleDto result = service.createSample(request);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void updateSample_matchingExperiment_updatesAndSaves() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "new insights", SamplingStatus.COMPLETED);
        Sample existing = Sample.builder().id(10L).experimentId(1L).build();
        Sample saved = Sample.builder().id(10L).experimentId(1L).samplingStatus(SamplingStatus.COMPLETED).build();
        SampleDto dto = mock(SampleDto.class);
        when(sampleRepository.getReferenceById(10L)).thenReturn(existing);
        when(sampleRepository.save(existing)).thenReturn(saved);
        when(sampleMapper.toDto(saved)).thenReturn(dto);

        SampleDto result = service.updateSample(request, 1L);

        assertThat(result).isEqualTo(dto);
        verify(sampleRequestMapper).applyUpdate(eq(request), eq(existing), anyLong());
    }

    @Test
    void updateSample_experimentIdMismatch_throwsUserExperimentException() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "new insights", SamplingStatus.COMPLETED);
        Sample existing = Sample.builder().id(10L).experimentId(1L).build();
        when(sampleRepository.getReferenceById(10L)).thenReturn(existing);

        assertThatThrownBy(() -> service.updateSample(request, 2L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(sampleRequestMapper);
        verify(sampleRepository, never()).save(any());
    }
}
