package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
import com.tuning.tuningprototype.models.mappers.data.ExperimentFinanceMapper;
import com.tuning.tuningprototype.services.entity.ExperimentService;
import com.tuning.tuningprototype.services.entity.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FinancialSummaryService {

    private final ExperimentService _experimentService;
    private final ExperimentFinanceMapper _experimentFinanceMapper;
    private final WalletService _walletService;


    public FinancialSummaryService(ExperimentService experimentService, ExperimentFinanceMapper experimentFinanceMapper, WalletService walletService) {
        _experimentService = experimentService;
        _experimentFinanceMapper = experimentFinanceMapper;
        _walletService = walletService;
    }

    /**
     * Fetches the experiment's summarized financial details (current money amounts and active quantities only) at the
     * provided checkTime in epoch seconds. Defaults to now if no time provided.
     *
     * @param experimentId The id of the experiment that we are summarizing the finances for.
     * @param asOf The point-in-time in epoch seconds that we are checking against.
     * @return Summarized experiment finances containing current money amounts and active quantities at the point in time.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public ExperimentFinances getExperimentFinancesAt(long experimentId, long asOf) {
        ExperimentDto experiment = _experimentService.getExperiment(experimentId, false)
                .orElseThrow(() -> new ExperimentException("Could not find experiment by id " + experimentId, true));
        List<WalletDto> walletsAtTime = _walletService.getWalletsByExperiment(experimentId, asOf);
        return _experimentFinanceMapper.toExperimentFinances(experiment, walletsAtTime, asOf);
    }
}
