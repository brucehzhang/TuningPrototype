package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.ExperimentDto;
import com.tuning.tuningprototype.models.enums.AgentModels;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("basicExperimentService")
public class BasicExperimentService implements IExperimentService {

    @Override
    public Optional<ExperimentDto> getExperiment(long id) {
        return Optional.of(new ExperimentDto(
                id,
                "Sample Experiment",
                AgentModels.CLAUDE_OPUS_4_8,
                "You are regarded and listen to wall street bets.",
                1721349014L,
                1752885014L,
                1784421014L,
                1784421014L));
    }
}
