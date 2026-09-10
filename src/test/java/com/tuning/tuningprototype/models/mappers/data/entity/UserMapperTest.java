package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.db.entity.UserDto;
import com.tuning.tuningprototype.models.enums.LicenseType;
import com.tuning.tuningprototype.testutil.UninitializedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private ExperimentMapper experimentMapper;

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper(experimentMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andExperiments_whenInitialized() {
        Experiment experiment = Experiment.builder().id(8L).build();
        ExperimentDto experimentDto = new ExperimentDto(
                8L, "e", null, null, null, null, null, null, null, null, null, null, null);
        when(experimentMapper.toDto(experiment)).thenReturn(experimentDto);

        List<Experiment> experiments = new ArrayList<>();
        experiments.add(experiment);

        User entity = User.builder()
                .id(1L)
                .firstName("Jane")
                .middleName("Q")
                .lastName("Doe")
                .username("jdoe")
                .email("jdoe@example.com")
                .passwordHash("hashed")
                .accountId(2L)
                .licenseType(LicenseType.PREMIUM)
                .createdTime(3000L)
                .modifiedTime(4000L)
                .experiments(experiments)
                .build();

        UserDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.firstName()).isEqualTo("Jane");
        assertThat(dto.middleName()).isEqualTo("Q");
        assertThat(dto.lastName()).isEqualTo("Doe");
        assertThat(dto.username()).isEqualTo("jdoe");
        assertThat(dto.email()).isEqualTo("jdoe@example.com");
        assertThat(dto.passwordHash()).isEqualTo("hashed");
        assertThat(dto.accountId()).isEqualTo(2L);
        assertThat(dto.licenseType()).isEqualTo(LicenseType.PREMIUM);
        assertThat(dto.createdTime()).isEqualTo(3000L);
        assertThat(dto.modifiedTime()).isEqualTo(4000L);
        assertThat(dto.experiments()).containsExactly(experimentDto);
    }

    @Test
    void toDto_experimentsIsNull_whenCollectionUninitialized() {
        User entity = User.builder()
                .id(1L)
                .experiments(new UninitializedList<>())
                .build();

        UserDto dto = mapper.toDto(entity);

        assertThat(dto.experiments()).isNull();
        verifyNoInteractions(experimentMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        UserDto dto = new UserDto(
                1L, "Jane", "Q", "Doe", "jdoe", "jdoe@example.com", "hashed",
                2L, LicenseType.FREE, 3000L, 4000L, List.of());

        User entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getFirstName()).isEqualTo("Jane");
        assertThat(entity.getMiddleName()).isEqualTo("Q");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getUsername()).isEqualTo("jdoe");
        assertThat(entity.getEmail()).isEqualTo("jdoe@example.com");
        assertThat(entity.getPasswordHash()).isEqualTo("hashed");
        assertThat(entity.getAccountId()).isEqualTo(2L);
        assertThat(entity.getLicenseType()).isEqualTo(LicenseType.FREE);
        assertThat(entity.getCreatedTime()).isEqualTo(3000L);
        assertThat(entity.getModifiedTime()).isEqualTo(4000L);
    }
}
