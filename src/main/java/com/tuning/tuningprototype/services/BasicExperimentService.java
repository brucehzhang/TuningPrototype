package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.ExperimentDto;
import com.tuning.tuningprototype.models.enums.AgentModel;
import com.tuning.tuningprototype.models.enums.ExperimentStatus;
import com.tuning.tuningprototype.models.enums.SamplingWindow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service("basicExperimentService")
public class BasicExperimentService implements IExperimentService {

    @Override
    public Optional<ExperimentDto> getExperiment(long id) {
        return Optional.of(new ExperimentDto(
                id,
                "Sample Experiment",
                AgentModel.CLAUDE_OPUS_4_8,
                "You are regarded and listen to wall street bets.",
                SamplingWindow.DAYS_1,
                BigDecimal.ONE,
                "USD",
                1721349014L,
                1752885014L,
                ExperimentStatus.DRAFT,
                1784421014L,
                123L,
                1784421014L));
    }
}
