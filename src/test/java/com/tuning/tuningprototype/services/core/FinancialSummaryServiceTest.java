package com.tuning.tuningprototype.services.core;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.ExperimentFinances;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
import com.tuning.tuningprototype.models.mappers.data.ExperimentFinanceMapper;
import com.tuning.tuningprototype.services.entity.ExperimentService;
import com.tuning.tuningprototype.services.entity.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialSummaryServiceTest {

    @Mock
    private ExperimentService experimentService;
    @Mock
    private ExperimentFinanceMapper experimentFinanceMapper;
    @Mock
    private WalletService walletService;

    private FinancialSummaryService service;

    @BeforeEach
    void setUp() {
        service = new FinancialSummaryService(experimentService, experimentFinanceMapper, walletService);
    }

    @Test
    void getExperimentFinancesAt_found_returnsMappedFinances() {
        ExperimentDto experimentDto = mock(ExperimentDto.class);
        when(experimentService.getExperiment(1L, false)).thenReturn(Optional.of(experimentDto));
        List<WalletDto> wallets = List.of(mock(WalletDto.class));
        when(walletService.getWalletsByExperiment(1L, 500L)).thenReturn(wallets);
        ExperimentFinances finances = new ExperimentFinances(1L, 500L, List.of());
        when(experimentFinanceMapper.toExperimentFinances(experimentDto, wallets, 500L)).thenReturn(finances);

        ExperimentFinances result = service.getExperimentFinancesAt(1L, 500L);

        assertThat(result).isEqualTo(finances);
    }

    @Test
    void getExperimentFinancesAt_nullAsOf_passedThroughToDependencies() {
        ExperimentDto experimentDto = mock(ExperimentDto.class);
        when(experimentService.getExperiment(1L, false)).thenReturn(Optional.of(experimentDto));
        List<WalletDto> wallets = List.of();
        when(walletService.getWalletsByExperiment(1L, null)).thenReturn(wallets);
        when(experimentFinanceMapper.toExperimentFinances(experimentDto, wallets, null))
                .thenReturn(new ExperimentFinances(1L, null, List.of()));

        ExperimentFinances result = service.getExperimentFinancesAt(1L, null);

        assertThat(result.asOf()).isNull();
        verify(walletService).getWalletsByExperiment(1L, null);
    }

    @Test
    void getExperimentFinancesAt_experimentNotFound_throwsUserExperimentException() {
        when(experimentService.getExperiment(1L, false)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getExperimentFinancesAt(1L, 500L))
                .isInstanceOf(ExperimentException.class)
                .satisfies(e -> assertThat(((ExperimentException) e).isUserError()).isTrue());

        verifyNoInteractions(walletService, experimentFinanceMapper);
    }
}
