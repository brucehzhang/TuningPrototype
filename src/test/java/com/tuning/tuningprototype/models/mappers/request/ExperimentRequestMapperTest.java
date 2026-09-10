package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Experiment;
import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExperimentRequestMapperTest {

    private final ExperimentRequestMapper mapper = new ExperimentRequestMapper();

    @Test
    void toEntity_mapsRequestFields_andSetsServerControlledFields() {
        CreateExperimentRequest request = new CreateExperimentRequest(
                "Experiment 1", AgentModel.CLAUDE_OPUS_4_7, "prompt", SamplingWindow.HOURS_1, 1000L, 2000L);

        Experiment entity = mapper.toEntity(request, 42L, 5000L);

        assertThat(entity.getName()).isEqualTo("Experiment 1");
        assertThat(entity.getAgentModel()).isEqualTo(AgentModel.CLAUDE_OPUS_4_7);
        assertThat(entity.getStrategyPrompt()).isEqualTo("prompt");
        assertThat(entity.getSamplingWindow()).isEqualTo(SamplingWindow.HOURS_1);
        assertThat(entity.getExperimentStartTime()).isEqualTo(1000L);
        assertThat(entity.getExperimentEndTime()).isEqualTo(2000L);
        assertThat(entity.getExperimentStatus()).isEqualTo(ExperimentStatus.DRAFT);
        assertThat(entity.getCreatedUserId()).isEqualTo(42L);
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }

    private Experiment existingExperiment() {
        return Experiment.builder()
                .id(1L)
                .name("Original")
                .agentModel(AgentModel.CLAUDE_HAIKU_4_5)
                .strategyPrompt("original prompt")
                .samplingWindow(SamplingWindow.MINUTES_15)
                .experimentStartTime(100L)
                .experimentEndTime(200L)
                .experimentStatus(ExperimentStatus.DRAFT)
                .createdUserId(1L)
                .createdTime(300L)
                .modifiedTime(400L)
                .build();
    }

    @Test
    void applyUpdate_overwritesFields_whenRequestFieldsAreNonNull() {
        Experiment existing = existingExperiment();
        UpdateExperimentRequest request = new UpdateExperimentRequest(
                1L, "Updated", AgentModel.CLAUDE_OPUS_5, "updated prompt", SamplingWindow.HOURS_2, 500L, 600L);

        mapper.applyUpdate(request, existing, 9000L);

        assertThat(existing.getName()).isEqualTo("Updated");
        assertThat(existing.getAgentModel()).isEqualTo(AgentModel.CLAUDE_OPUS_5);
        assertThat(existing.getStrategyPrompt()).isEqualTo("updated prompt");
        assertThat(existing.getSamplingWindow()).isEqualTo(SamplingWindow.HOURS_2);
        assertThat(existing.getExperimentStartTime()).isEqualTo(500L);
        assertThat(existing.getExperimentEndTime()).isEqualTo(600L);
        assertThat(existing.getModifiedTime()).isEqualTo(9000L);
    }

    @Test
    void applyUpdate_leavesExistingValues_whenRequestFieldsAreNull() {
        Experiment existing = existingExperiment();
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, null, null, null, null, null, null);

        mapper.applyUpdate(request, existing, 9000L);

        assertThat(existing.getName()).isEqualTo("Original");
        assertThat(existing.getAgentModel()).isEqualTo(AgentModel.CLAUDE_HAIKU_4_5);
        assertThat(existing.getStrategyPrompt()).isEqualTo("original prompt");
        assertThat(existing.getSamplingWindow()).isEqualTo(SamplingWindow.MINUTES_15);
        assertThat(existing.getExperimentStartTime()).isEqualTo(100L);
        assertThat(existing.getExperimentEndTime()).isEqualTo(200L);
        // modifiedTime is always updated, regardless of what else changed
        assertThat(existing.getModifiedTime()).isEqualTo(9000L);
    }
}
