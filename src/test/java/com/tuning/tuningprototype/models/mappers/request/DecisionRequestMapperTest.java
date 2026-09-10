package com.tuning.tuningprototype.models.mappers.request;

import com.tuning.tuningprototype.models.db.entity.Decision;
import com.tuning.tuningprototype.models.enums.DecisionType;
import com.tuning.tuningprototype.models.requests.CreateDecisionRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionRequestMapperTest {

    private final DecisionRequestMapper mapper = new DecisionRequestMapper();

    @Test
    void toEntity_mapsRequestFields_andSetsServerControlledTimestamps() {
        CreateDecisionRequest request = new CreateDecisionRequest(
                1L, DecisionType.BUY, "AAPL", "strong earnings", 1000L);

        Decision entity = mapper.toEntity(request, 5000L);

        assertThat(entity.getSampleId()).isEqualTo(1L);
        assertThat(entity.getDecisionType()).isEqualTo(DecisionType.BUY);
        assertThat(entity.getTicker()).isEqualTo("AAPL");
        assertThat(entity.getReasoning()).isEqualTo("strong earnings");
        assertThat(entity.getDecisionTime()).isEqualTo(1000L);
        assertThat(entity.getCreatedTime()).isEqualTo(5000L);
        assertThat(entity.getModifiedTime()).isEqualTo(5000L);
    }
}
