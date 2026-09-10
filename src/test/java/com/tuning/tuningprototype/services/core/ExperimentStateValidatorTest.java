package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperimentStateValidatorTest {

    @Mock
    private ExperimentRepository experimentRepository;

    private ExperimentStateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ExperimentStateValidator(experimentRepository);
    }

    @Test
    void getStatus_found_returnsStatus() {
        Experiment experiment = Experiment.builder().id(1L).experimentStatus(ExperimentStatus.DRAFT).build();
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));

        ExperimentStatus status = validator.getStatus(1L);

        assertThat(status).isEqualTo(ExperimentStatus.DRAFT);
    }

    @Test
    void getStatus_notFound_throwsEntityNotFoundException() {
        when(experimentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validator.getStatus(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("1");
    }
}
