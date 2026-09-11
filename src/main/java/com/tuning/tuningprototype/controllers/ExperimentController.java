package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.entity.ExperimentDto;
import com.tuning.tuningprototype.models.db.entity.SampleDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExperimentController {

    private static final Logger log = LoggerFactory.getLogger(ExperimentController.class);

    private final DecisionMakingService _decisionMakingService;
    private final ExperimentService _experimentService;
    private final ExperimentProcessorService _experimentProcessorService;
    private final FinancialSummaryService _financialSummaryService;
    private final SampleService _sampleService;
    private final WalletService _walletService;

    public ExperimentController(DecisionMakingService decisionMakingService, ExperimentService experimentService, ExperimentProcessorService experimentProcessorService, FinancialSummaryService financialSummaryService, SampleService sampleService, WalletService walletService) {
        _decisionMakingService = decisionMakingService;
        _experimentService = experimentService;
        _experimentProcessorService = experimentProcessorService;
        _financialSummaryService = financialSummaryService;
        _sampleService = sampleService;
        _walletService = walletService;
    }

    /**
     * Gets an experiment by the id.
     *
     * @param id - id of the experiment
     * @param shouldDecorate - Boolean flag toggling whether we do additional querying for nested data.
     * @return ExperimentDto record, 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExperimentDto> getExperiment(@PathVariable long id,
                                                       @RequestParam(value = "shouldDecorate", defaultValue = "true") boolean shouldDecorate) {
        return ResponseEntity.of(_experimentService.getExperiment(id, shouldDecorate));
    }

    /**
     * Gets experiments by the user id.
     *
     * @param userId - id of the user to get experiments by
     * @param pageable - Pageable containing page size, page number, and sorting for the experiment query. Pages start at 0.
     * @return Page of ExperimentDto records, or 500 with the error
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getExperimentsByUser(@PathVariable long userId,
                                                  @PageableDefault(page = 1, size = 20, sort = "createdTime", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            return ResponseEntity.ok(new PagedModel<>(_experimentService.getExperimentsByUser(userId, pageable)));
        } catch (Exception e) {
            String message = "Exception occurred getting experiments for user %s: %s".formatted(userId, e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Creates an experiment in DRAFT state
     *
     * @param createExperimentRequest The details for creating the experiment
     * @param createdUserId The user that created the experiment
     * @return The DTO for the created experiment, or 500 with the error
     */
    @PostMapping
    public ResponseEntity<?> createExperiment(@RequestBody CreateExperimentRequest createExperimentRequest,
                                                          @RequestParam long createdUserId) {
        try {
            log.info("Received createExperiment request: {}", createExperimentRequest);
            return ResponseEntity.ok(_experimentService.createExperiment(createExperimentRequest, createdUserId));
        } catch (Exception e) {
            String message = "Exception occurred creating experiment: %s".formatted(e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Updates an experiment with the provided body
     *
     * @param updateExperimentRequest The request body containing details to update the experiment with
     * @return The DTO of the updated experiment, or 500 with the error
     */
    @PatchMapping
    public ResponseEntity<?> updateExperiment(@RequestBody UpdateExperimentRequest updateExperimentRequest) {
        try {
            log.info("Received updateExperiment request: {}", updateExperimentRequest);
            return ResponseEntity.ok(_experimentService.updateExperiment(updateExperimentRequest));
        } catch (Exception e) {
            String message = "Exception occurred updating experiment: %s".formatted(e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Creates a Sample in IN_PROGRESS state. Used for manual triggering of sample creation in the event of issues in the
     * experiment loop. This is NOT recommended for direct use.
     *
     * @param experimentId The id of the experiment that the sample will belong to, used for validation and REST pathing.
     * @param createSampleRequest The details for creating the sample
     * @return The DTO for the created sample, or 500 with the error
     */
    @PostMapping("/{experimentId}/samples")
    public ResponseEntity<?> createSample(@PathVariable("experimentId") long experimentId, @RequestBody CreateSampleRequest createSampleRequest) {
        try {
            log.info("Received createSample request for experiment {}: {}", experimentId, createSampleRequest);
            if (experimentId != createSampleRequest.experimentId()) {
                throw new ExperimentException("Experiment id between path and body do not match.", true);
            }
            return ResponseEntity.ok(_experimentProcessorService.startSampling(createSampleRequest));
        } catch (Exception e) {
            String message = "Exception occurred creating sample for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Updates a sample with the provided body, used for storing analyzed market data into market insights and modifying
     * the state of the sample. If updated to COMPLETED, will trigger the next Sample if the next sample is within the
     * experiment's end time.
     *
     * @param experimentId The id of the experiment that the sample will belong to, used for validation and REST pathing.
     * @param updateSampleRequest The request body containing details to update the sample with
     * @return The DTO of the updated sample + optionally created next sample DTO, or 500 with the error
     */
    @PatchMapping("/{experimentId}/samples")
    @McpTool(description = "Updates the sample with the market insights and sets sample state to DECIDING/COMPLETED/FAILED. If marking to COMPLETED, " +
            "will trigger the next Sample if the next sampling time is within the experiment's end time.")
    public ResponseEntity<?> updateSample(
            @McpToolParam(description = "The id of the experiment that the sample belongs to.")
            @PathVariable("experimentId") long experimentId,
            @McpToolParam(description = "The request body containing details to update the experiment with. " +
                    "Market insights will be updated with by the agent after its tool calls for market data have been summarized, at which the state will be set to DECIDING. " +
                    "The status will be updated to complete after all trade decisions have been COMPLETED, or FAILED if anything goes wrong.")
            @RequestBody UpdateSampleRequest updateSampleRequest) {
        try {
            log.info("Received updateSample request for experiment {}: {}", experimentId, updateSampleRequest);
            SampleDto sampleDto = _sampleService.updateSample(updateSampleRequest, experimentId);
            Optional<SampleDto> optCreatedSample = Optional.empty();
            if (SamplingStatus.COMPLETED.equals(updateSampleRequest.samplingStatus())) {
                log.info("Sample {} was updated to COMPLETED, checking if next Sample is necessary.", sampleDto.id());
                // TODO:: Review this, probably want to wrap in transaction together with updating the Sample.
                optCreatedSample = _experimentProcessorService.continueSampling(experimentId, sampleDto);
            }
            return ResponseEntity.ok(new SamplingResponse(sampleDto, optCreatedSample.orElse(null)));
        } catch (Exception e) {
            String message = "Exception occurred updating sample for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Starts the experiment if it is in DRAFT state. The experiment start time will drive if the sampling.
     * If it is in the past, then immediate sampling using historic market data will be done.
     * If it is in the future, then scheduled sampling will be done.
     *
     * @param experimentId Id of the experiment
     * @return The DTO of the started experiment, 500 for server error.
     */
    @PostMapping("/{experimentId}/run")
    public ResponseEntity<?> runExperiment(@PathVariable("experimentId") long experimentId) {
        try {
            log.info("Received runExperiment request: {}", experimentId);
            return ResponseEntity.ok(_experimentProcessorService.runExperiment(experimentId));
        } catch (Exception e) {
            String message = "Exception occurred running experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Creates a Wallet containing starting money amount and currency associated to an experiment.
     *
     * @param experimentId The id of the experiment that the wallet will belong to, used for validation and REST pathing.
     * @param createWalletRequest The details for creating the wallet
     * @return The DTO for the created wallet, or 500 with the error
     */
    @PostMapping("/{experimentId}/wallets")
    public ResponseEntity<?> createWallet(@PathVariable("experimentId") long experimentId,
                                          @RequestBody CreateWalletRequest createWalletRequest) {
        try {
            log.info("Received createWallet request for experiment {}: {}", experimentId, createWalletRequest);
            if (experimentId != createWalletRequest.experimentId()) {
                throw new ExperimentException("Experiment id between path and body do not match.", true);
            }
            ExperimentDto experimentDto = _experimentService.getExperiment(experimentId, false)
                    .orElseThrow(() -> new ExperimentException("No experiment found for the provided id %s".formatted(experimentId), true));
            return ResponseEntity.ok(_walletService.createWallet(createWalletRequest, experimentDto));
        } catch (Exception e) {
            String message = "Exception occurred creating wallet for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);

        }
    }

    /**
     * Fetches all the wallets for an experiment along with purchase and sales decisions at the provided checkTime
     * in epoch seconds. Defaults to now if no time provided.
     *
     * @param experimentId The id of the experiment that we are fetching the wallets from.
     * @param checkTime The point-in-time in epoch seconds that we are checking against.
     * @return List of Wallet DTOs with the associated purchases and sales at the point in time.
     */
    @GetMapping("/{experimentId}/wallets")
    @McpTool(description = "Fetches all the wallets for an experiment along with purchase and sales decisions " +
            "at the provided checkTime in epoch seconds. Defaults to now if no time provided.")
    public ResponseEntity<?> getExperimentWallets(
            @McpToolParam(description = "The id of the experiment that we are fetching the wallets from.")
            @PathVariable("experimentId") long experimentId,
            @McpToolParam(description = "The point-in-time in epoch seconds that we are checking against.")
            @RequestParam(required = false) Long checkTime) {
        try {
            return ResponseEntity.ok(_walletService.getWalletsByExperiment(experimentId, checkTime));
        } catch (Exception e) {
            String message = "Exception occurred getting wallets for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Fetches the experiment's summarized financial details (current money amounts and active quantities only) at the
     * provided checkTime in epoch seconds. Defaults to now if no time provided.
     *
     * @param experimentId The id of the experiment that we are summarizing the finances for.
     * @param checkTime The point-in-time in epoch seconds that we are checking against.
     * @return Summarized experiment finances containing current money amounts and active quantities at the point in time.
     */
    @GetMapping("/{experimentId}/finances")
    @McpTool(description = "Fetches the experiment's summarized financial details (current money amounts and active " +
            "quantities only) at the provided checkTime in epoch seconds. Defaults to now if no time provided.")
    public ResponseEntity<?> getExperimentFinances(
            @McpToolParam(description = "The id of the experiment that we are summarizing the finances for.")
            @PathVariable("experimentId") long experimentId,
            @McpToolParam(description = "The point-in-time in epoch seconds that we are checking against.")
            @RequestParam(required = false) Long checkTime) {
        try {
            return ResponseEntity.ok(_financialSummaryService.getExperimentFinancesAt(experimentId, checkTime));
        } catch (Exception e) {
            String message = "Exception occurred getting summarized finances for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    /**
     * Updates a wallet with the provided body containing changes to the starting amount and/or currency.
     *
     * @param experimentId The id of the experiment that the wallet will belong to, used for validation and REST pathing.
     * @param updateWalletRequest The request body containing details to update the wallet with
     * @return The DTO of the updated sample, or 500 with the error
     */
    @PatchMapping("/{experimentId}/wallets")
    public ResponseEntity<?> updateWallet(
            @PathVariable("experimentId") long experimentId,
            @RequestBody UpdateWalletRequest updateWalletRequest) {
        try {
            log.info("Received updateWallet request for experiment {}: {}", experimentId, updateWalletRequest);
            return ResponseEntity.ok(_walletService.updateStartingWallet(updateWalletRequest, experimentId));
        } catch (Exception e) {
            String message = "Exception occurred updating wallet for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    @PostMapping("/{experimentId}/samples/{sampleId}/decisions")
    @McpTool(description = "Creates decisions and associated asset sales and purchase lots. This should be used as part of" +
            "the decision-making of the experiment's agent after it has reviewed its strategy, finances, and market analysis." +
            "All decisions for a sample should be made in one go and should happen while the sample is in DECIDING state.")
    public ResponseEntity<?> makeDecisions(
            @McpToolParam(description = "The id of the experiment that we are making decisions for.")
            @PathVariable("experimentId") long experimentId,
            @McpToolParam(description = "The id of the sample that the decisions will be made under.")
            @PathVariable("sampleId") long sampleId,
            @McpToolParam(description = "The decisions and associated purchases/sales that are being made in this sampling round.")
            @RequestBody MakeDecisionsRequest makeDecisionsRequest) {
        try {
            log.info("Received makeDecisions request for experiment {} sample {}: {}", experimentId, sampleId, makeDecisionsRequest);
            return ResponseEntity.ok(_decisionMakingService.makeDecisions(makeDecisionsRequest, sampleId));
        } catch (Exception e) {
            String message = "Exception occurred making decisions for experiment %s: %s".formatted(experimentId, e.getMessage());
            return handleException(e, message);
        }
    }

    // shared exception handling method
    private ResponseEntity<?> handleException(Exception e, String message) {
        ErrorResponse response = new ErrorResponse(message);
        if (e instanceof ExperimentException && ((ExperimentException) e).isUserError()) {
            log.warn(message);
            return ResponseEntity.badRequest().body(response);
        }
        log.error(message);
        return ResponseEntity.internalServerError().body(response);
    }
}
