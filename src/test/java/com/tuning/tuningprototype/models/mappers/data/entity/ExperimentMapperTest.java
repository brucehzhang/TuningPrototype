package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.Sample;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.db.entity.Wallet;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;
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
class ExperimentMapperTest {

    @Mock
    private SampleMapper sampleMapper;

    @Mock
    private WalletMapper walletMapper;

    private ExperimentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExperimentMapper(sampleMapper, walletMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andNestedCollections_whenInitialized() {
        Sample sample = Sample.builder().id(5L).build();
        SampleDto sampleDto = new SampleDto(5L, 1L, null, null, null, null, null, null);
        when(sampleMapper.toDto(sample)).thenReturn(sampleDto);

        Wallet wallet = Wallet.builder().id(6L).build();
        WalletDto walletDto = new WalletDto(6L, 1L, null, null, null, null, null, null);
        when(walletMapper.toDto(wallet)).thenReturn(walletDto);

        List<Sample> samples = new ArrayList<>();
        samples.add(sample);
        List<Wallet> wallets = new ArrayList<>();
        wallets.add(wallet);

        Experiment entity = Experiment.builder()
                .id(1L)
                .name("Experiment 1")
                .agentModel(AgentModel.CLAUDE_OPUS_4_7)
                .strategyPrompt("prompt")
                .samplingWindow(SamplingWindow.HOURS_1)
                .experimentStartTime(1000L)
                .experimentEndTime(2000L)
                .experimentStatus(ExperimentStatus.IN_PROGRESS)
                .createdTime(3000L)
                .createdUserId(4L)
                .modifiedTime(5000L)
                .samples(samples)
                .wallets(wallets)
                .build();

        ExperimentDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Experiment 1");
        assertThat(dto.agentModel()).isEqualTo(AgentModel.CLAUDE_OPUS_4_7);
        assertThat(dto.strategyPrompt()).isEqualTo("prompt");
        assertThat(dto.samplingWindow()).isEqualTo(SamplingWindow.HOURS_1);
        assertThat(dto.experimentStartTime()).isEqualTo(1000L);
        assertThat(dto.experimentEndTime()).isEqualTo(2000L);
        assertThat(dto.experimentStatus()).isEqualTo(ExperimentStatus.IN_PROGRESS);
        assertThat(dto.createdTime()).isEqualTo(3000L);
        assertThat(dto.createdUserId()).isEqualTo(4L);
        assertThat(dto.modifiedTime()).isEqualTo(5000L);
        assertThat(dto.samples()).containsExactly(sampleDto);
        assertThat(dto.wallets()).containsExactly(walletDto);
    }

    @Test
    void toDto_nestedCollectionsAreNull_whenUninitialized() {
        Experiment entity = Experiment.builder()
                .id(1L)
                .samples(new UninitializedList<>())
                .wallets(new UninitializedList<>())
                .build();

        ExperimentDto dto = mapper.toDto(entity);

        assertThat(dto.samples()).isNull();
        assertThat(dto.wallets()).isNull();
        verifyNoInteractions(sampleMapper, walletMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        ExperimentDto dto = new ExperimentDto(
                1L, "Experiment 1", AgentModel.CLAUDE_SONNET_5, "prompt", SamplingWindow.DAYS_1,
                1000L, 2000L, ExperimentStatus.DRAFT, 3000L, 4L, 5000L, List.of(), List.of());

        Experiment entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Experiment 1");
        assertThat(entity.getAgentModel()).isEqualTo(AgentModel.CLAUDE_SONNET_5);
        assertThat(entity.getStrategyPrompt()).isEqualTo("prompt");
        assertThat(entity.getSamplingWindow()).isEqualTo(SamplingWindow.DAYS_1);
        assertThat(entity.getExperimentStartTime()).isEqualTo(1000L);
        assertThat(entity.getExperimentEndTime()).isEqualTo(2000L);
        assertThat(entity.getExperimentStatus()).isEqualTo(ExperimentStatus.DRAFT);
        assertThat(entity.getCreatedTime()).isEqualTo(3000L);
        assertThat(entity.getCreatedUserId()).isEqualTo(4L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }
}
