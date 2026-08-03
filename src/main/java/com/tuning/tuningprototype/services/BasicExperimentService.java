package com.tuning.tuningprototype.services;

import com.tuning.tuningprototype.models.db.Experiment;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.mappers.ExperimentMapper;
import com.tuning.tuningprototype.repositories.ExperimentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("basicExperimentService")
public class BasicExperimentService implements IExperimentService {

    private final ExperimentRepository _experimentRepository;
    private final ExperimentMapper _experimentMapper;

    public BasicExperimentService(ExperimentRepository experimentRepository, ExperimentMapper experimentMapper) {
        _experimentRepository = experimentRepository;
        _experimentMapper = experimentMapper;
    }

    @Override
    public Optional<ExperimentDto> getExperiment(long id) {
        // Using wallet query, but lazy loading samples with hibernate through @BatchSize
        return _experimentRepository.findWithWalletsById(id)
                .map(this::loadFullyHydratedExperiment)
                .map(_experimentMapper::toDto);
    }

    // Used for completely hydrating an experiment
    // Expensive, do not use for bulk.
    // TODO:: Come back and review this with pagination in mind.
    private Experiment loadFullyHydratedExperiment(Experiment experiment) {
        // These forEach and size calls allow Hibernate to load the nested data.
        experiment.getWallets()
                .forEach(wallet -> wallet.getPurchaseLots()
                        .forEach(purchaseLot -> purchaseLot.getAssetSales().size()));
        experiment.getSamples().forEach(sample -> sample.getDecisions().forEach(decision -> {
            decision.getAssetSales().size();
            decision.getPurchaseLots().size();
        }));
        return experiment;
    }
}
