package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.DecisionDto;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
import com.tuning.tuningprototype.models.db.entity.WalletDto;
import com.tuning.tuningprototype.models.enums.SamplingStatus;
import com.tuning.tuningprototype.models.requests.*;
import com.tuning.tuningprototype.models.responses.ErrorResponse;
import com.tuning.tuningprototype.models.responses.SamplingResponse;
import com.tuning.tuningprototype.services.core.DecisionMakingService;
import com.tuning.tuningprototype.services.core.ExperimentProcessorService;
import com.tuning.tuningprototype.services.core.FinancialSummaryService;
import com.tuning.tuningprototype.services.entity.ExperimentService;
import com.tuning.tuningprototype.services.entity.SampleService;
import com.tuning.tuningprototype.services.entity.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperimentControllerTest {

    @Mock
    private DecisionMakingService decisionMakingService;
    @Mock
    private ExperimentService experimentService;
    @Mock
    private ExperimentProcessorService experimentProcessorService;
    @Mock
    private FinancialSummaryService financialSummaryService;
    @Mock
    private SampleService sampleService;
    @Mock
    private WalletService walletService;

    private ExperimentController controller;

    @BeforeEach
    void setUp() {
        controller = new ExperimentController(decisionMakingService, experimentService, experimentProcessorService,
                financialSummaryService, sampleService, walletService);
    }

    private ExperimentDto sampleExperimentDto(long id) {
        return new ExperimentDto(id, "name", null, "prompt", null, 100L, 200L, null, 1L, 1L, 1L, List.of(), List.of());
    }

    private SampleDto sampleSampleDto(long id, long experimentId, SamplingStatus status) {
        return new SampleDto(id, experimentId, "insights", 100L, status, 1L, 1L, List.of());
    }

    // --- getExperiment ---

    @Test
    void getExperiment_found_returns200WithBody() {
        ExperimentDto dto = sampleExperimentDto(1L);
        when(experimentService.getExperiment(1L, true)).thenReturn(Optional.of(dto));

        var response = controller.getExperiment(1L, true);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void getExperiment_notFound_returns404() {
        when(experimentService.getExperiment(1L, true)).thenReturn(Optional.empty());

        var response = controller.getExperiment(1L, true);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    // --- getExperimentsByUser ---
    @Test
    void getExperimentsByUser_success_returns200WithBody() {
        ExperimentDto dto = sampleExperimentDto(1L);
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<ExperimentDto> page = new PageImpl<>(List.of(dto), pageable, 1);
        when(experimentService.getExperimentsByUser(1L, pageable)).thenReturn(page);

        var response = controller.getExperimentsByUser(1L, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        PagedModel<ExperimentDto> body = (PagedModel<ExperimentDto>) response.getBody();
        assertThat(body.getContent()).containsExactly(dto);
    }

    @Test
    void getExperimentsByUser_userError_returns400() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdTime"));
        when(experimentService.getExperimentsByUser(1L, pageable))
                .thenThrow(new ExperimentException("bad", true));

        var response = controller.getExperimentsByUser(1L, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getExperimentsByUser_genericException_returns500() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdTime"));
        when(experimentService.getExperimentsByUser(1L, pageable)).thenThrow(new RuntimeException("boom"));

        var response = controller.getExperimentsByUser(1L, pageable);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- createExperiment ---

    @Test
    void createExperiment_success_returns200WithBody() {
        CreateExperimentRequest request = new CreateExperimentRequest("name", null, "prompt", null, 100L, 200L);
        ExperimentDto dto = sampleExperimentDto(1L);
        when(experimentService.createExperiment(request, 5L)).thenReturn(dto);

        var response = controller.createExperiment(request, 5L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void createExperiment_userError_returns400WithErrorResponse() {
        CreateExperimentRequest request = new CreateExperimentRequest("name", null, "prompt", null, 100L, 200L);
        when(experimentService.createExperiment(request, 5L))
                .thenThrow(new ExperimentException("bad request", true));

        var response = controller.createExperiment(request, 5L);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    @Test
    void createExperiment_genericException_returns500WithErrorResponse() {
        CreateExperimentRequest request = new CreateExperimentRequest("name", null, "prompt", null, 100L, 200L);
        when(experimentService.createExperiment(request, 5L))
                .thenThrow(new RuntimeException("boom"));

        var response = controller.createExperiment(request, 5L);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    // --- updateExperiment ---

    @Test
    void updateExperiment_success_returns200WithBody() {
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, "name", null, "prompt", null, 100L, 200L);
        ExperimentDto dto = sampleExperimentDto(1L);
        when(experimentService.updateExperiment(request)).thenReturn(dto);

        var response = controller.updateExperiment(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void updateExperiment_notInDraftState_returns400() {
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, "name", null, "prompt", null, 100L, 200L);
        when(experimentService.updateExperiment(request))
                .thenThrow(new ExperimentException("not draft", true));

        var response = controller.updateExperiment(request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void updateExperiment_nonUserExperimentException_returns500() {
        UpdateExperimentRequest request = new UpdateExperimentRequest(1L, "name", null, "prompt", null, 100L, 200L);
        when(experimentService.updateExperiment(request))
                .thenThrow(new ExperimentException("server error", false));

        var response = controller.updateExperiment(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- createSample ---

    @Test
    void createSample_success_returns200WithBody() {
        CreateSampleRequest request = new CreateSampleRequest(1L, "insights", 100L);
        SampleDto dto = sampleSampleDto(10L, 1L, SamplingStatus.IN_PROGRESS);
        when(experimentProcessorService.startSampling(request)).thenReturn(dto);

        var response = controller.createSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void createSample_idMismatch_returns400() {
        CreateSampleRequest request = new CreateSampleRequest(2L, "insights", 100L);

        var response = controller.createSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        verifyNoInteractions(experimentProcessorService);
    }

    @Test
    void createSample_serviceThrowsGenericException_returns500() {
        CreateSampleRequest request = new CreateSampleRequest(1L, "insights", 100L);
        when(experimentProcessorService.startSampling(request)).thenThrow(new RuntimeException("boom"));

        var response = controller.createSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- updateSample ---

    @Test
    void updateSample_notCompleted_returns200WithoutContinuingSampling() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "insights", SamplingStatus.DECIDING);
        SampleDto updated = sampleSampleDto(10L, 1L, SamplingStatus.DECIDING);
        when(sampleService.updateSample(request, 1L)).thenReturn(updated);

        var response = controller.updateSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        SamplingResponse body = (SamplingResponse) response.getBody();
        assertThat(body.updatedSample()).isEqualTo(updated);
        assertThat(body.nextCreatedSample()).isNull();
        verify(experimentProcessorService, never()).continueSampling(anyLong(), any());
    }

    @Test
    void updateSample_completed_triggersContinueSamplingAndReturnsNextSample() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "insights", SamplingStatus.COMPLETED);
        SampleDto updated = sampleSampleDto(10L, 1L, SamplingStatus.COMPLETED);
        SampleDto next = sampleSampleDto(11L, 1L, SamplingStatus.IN_PROGRESS);
        when(sampleService.updateSample(request, 1L)).thenReturn(updated);
        when(experimentProcessorService.continueSampling(1L, updated)).thenReturn(Optional.of(next));

        var response = controller.updateSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        SamplingResponse body = (SamplingResponse) response.getBody();
        assertThat(body.updatedSample()).isEqualTo(updated);
        assertThat(body.nextCreatedSample()).isEqualTo(next);
    }

    @Test
    void updateSample_completedButNoNextSample_returnsNullNextSample() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "insights", SamplingStatus.COMPLETED);
        SampleDto updated = sampleSampleDto(10L, 1L, SamplingStatus.COMPLETED);
        when(sampleService.updateSample(request, 1L)).thenReturn(updated);
        when(experimentProcessorService.continueSampling(1L, updated)).thenReturn(Optional.empty());

        var response = controller.updateSample(1L, request);

        SamplingResponse body = (SamplingResponse) response.getBody();
        assertThat(body.nextCreatedSample()).isNull();
    }

    @Test
    void updateSample_userError_returns400() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "insights", SamplingStatus.DECIDING);
        when(sampleService.updateSample(request, 1L)).thenThrow(new ExperimentException("mismatch", true));

        var response = controller.updateSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void updateSample_genericException_returns500() {
        UpdateSampleRequest request = new UpdateSampleRequest(10L, "insights", SamplingStatus.DECIDING);
        when(sampleService.updateSample(request, 1L)).thenThrow(new RuntimeException("boom"));

        var response = controller.updateSample(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- runExperiment ---

    @Test
    void runExperiment_success_returns200WithBody() {
        ExperimentDto dto = sampleExperimentDto(1L);
        when(experimentProcessorService.runExperiment(1L)).thenReturn(dto);

        var response = controller.runExperiment(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void runExperiment_notInDraft_returns400() {
        when(experimentProcessorService.runExperiment(1L)).thenThrow(new ExperimentException("not draft", true));

        var response = controller.runExperiment(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void runExperiment_genericException_returns500() {
        when(experimentProcessorService.runExperiment(1L)).thenThrow(new RuntimeException("boom"));

        var response = controller.runExperiment(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- createWallet ---

    @Test
    void createWallet_success_returns200WithBody() {
        CreateWalletRequest request = new CreateWalletRequest(1L, BigDecimal.TEN, 100L, "USD");
        ExperimentDto experimentDto = sampleExperimentDto(1L);
        WalletDto walletDto = new WalletDto(5L, 1L, BigDecimal.TEN, 100L, "USD", 1L, 1L, List.of());
        when(experimentService.getExperiment(1L, false)).thenReturn(Optional.of(experimentDto));
        when(walletService.createWallet(request, experimentDto)).thenReturn(walletDto);

        var response = controller.createWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(walletDto);
    }

    @Test
    void createWallet_idMismatch_returns400() {
        CreateWalletRequest request = new CreateWalletRequest(2L, BigDecimal.TEN, 100L, "USD");

        var response = controller.createWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        verifyNoInteractions(experimentService, walletService);
    }

    @Test
    void createWallet_experimentNotFound_returns400() {
        CreateWalletRequest request = new CreateWalletRequest(1L, BigDecimal.TEN, 100L, "USD");
        when(experimentService.getExperiment(1L, false)).thenReturn(Optional.empty());

        var response = controller.createWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void createWallet_genericException_returns500() {
        CreateWalletRequest request = new CreateWalletRequest(1L, BigDecimal.TEN, 100L, "USD");
        when(experimentService.getExperiment(1L, false)).thenThrow(new RuntimeException("boom"));

        var response = controller.createWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- getExperimentWallets ---

    @Test
    void getExperimentWallets_success_returns200WithBody() {
        List<WalletDto> wallets = List.of(new WalletDto(5L, 1L, BigDecimal.TEN, 100L, "USD", 1L, 1L, List.of()));
        when(walletService.getWalletsByExperiment(1L, 500L)).thenReturn(wallets);

        var response = controller.getExperimentWallets(1L, 500L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(wallets);
    }

    @Test
    void getExperimentWallets_userError_returns400() {
        when(walletService.getWalletsByExperiment(1L, null)).thenThrow(new ExperimentException("bad", true));

        var response = controller.getExperimentWallets(1L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getExperimentWallets_genericException_returns500() {
        when(walletService.getWalletsByExperiment(1L, null)).thenThrow(new RuntimeException("boom"));

        var response = controller.getExperimentWallets(1L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- getExperimentFinances ---

    @Test
    void getExperimentFinances_success_returns200WithBody() {
        var finances = new com.tuning.tuningprototype.models.db.ExperimentFinances(1L, 500L, List.of());
        when(financialSummaryService.getExperimentFinancesAt(1L, 500L)).thenReturn(finances);

        var response = controller.getExperimentFinances(1L, 500L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(finances);
    }

    @Test
    void getExperimentFinances_userError_returns400() {
        when(financialSummaryService.getExperimentFinancesAt(1L, null))
                .thenThrow(new ExperimentException("not found", true));

        var response = controller.getExperimentFinances(1L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void getExperimentFinances_genericException_returns500() {
        when(financialSummaryService.getExperimentFinancesAt(1L, null)).thenThrow(new RuntimeException("boom"));

        var response = controller.getExperimentFinances(1L, null);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- updateWallet ---

    @Test
    void updateWallet_success_returns200WithBody() {
        UpdateWalletRequest request = new UpdateWalletRequest(5L, BigDecimal.TEN, "USD");
        WalletDto walletDto = new WalletDto(5L, 1L, BigDecimal.TEN, 100L, "USD", 1L, 1L, List.of());
        when(walletService.updateStartingWallet(request, 1L)).thenReturn(walletDto);

        var response = controller.updateWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(walletDto);
    }

    @Test
    void updateWallet_notInDraft_returns400() {
        UpdateWalletRequest request = new UpdateWalletRequest(5L, BigDecimal.TEN, "USD");
        when(walletService.updateStartingWallet(request, 1L)).thenThrow(new ExperimentException("not draft", true));

        var response = controller.updateWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void updateWallet_genericException_returns500() {
        UpdateWalletRequest request = new UpdateWalletRequest(5L, BigDecimal.TEN, "USD");
        when(walletService.updateStartingWallet(request, 1L)).thenThrow(new RuntimeException("boom"));

        var response = controller.updateWallet(1L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }

    // --- makeDecisions ---

    @Test
    void makeDecisions_success_returns200WithBody() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of());
        List<DecisionDto> decisions = List.of();
        when(decisionMakingService.makeDecisions(request, 20L)).thenReturn(decisions);

        var response = controller.makeDecisions(1L, 20L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(decisions);
    }

    @Test
    void makeDecisions_userError_returns400() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of());
        when(decisionMakingService.makeDecisions(request, 20L))
                .thenThrow(new ExperimentException("sample mismatch", true));

        var response = controller.makeDecisions(1L, 20L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void makeDecisions_genericException_returns500() {
        MakeDecisionsRequest request = new MakeDecisionsRequest(List.of());
        when(decisionMakingService.makeDecisions(request, 20L)).thenThrow(new RuntimeException("boom"));

        var response = controller.makeDecisions(1L, 20L, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
    }
}
