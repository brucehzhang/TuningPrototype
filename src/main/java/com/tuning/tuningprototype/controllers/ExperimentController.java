package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.exceptions.ExperimentException;
import com.tuning.tuningprototype.models.db.ExperimentDto;
import com.tuning.tuningprototype.models.requests.CreateExperimentRequest;
import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import com.tuning.tuningprototype.models.requests.UpdateExperimentRequest;
import com.tuning.tuningprototype.models.requests.UpdateSampleRequest;
import com.tuning.tuningprototype.services.ExperimentService;
import com.tuning.tuningprototype.services.SampleService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExperimentController {

    private final ExperimentService _experimentService;
    private final SampleService _sampleService;

    public ExperimentController(ExperimentService experimentService, SampleService sampleService) {
        _experimentService = experimentService;
        _sampleService = sampleService;
    }

    /**
     * Heartbeat for application.
     *
     * @return 200 indicating that application is alive.
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            return ResponseEntity.ok().body("Service is online");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Experiments Internal Error: %s", e.getMessage()));
        }
    }

    /**
     * Gets an experiment by the id.
     *
     * @param id - id of the experiment
     * @return ExperimentDto record
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExperimentDto> getExperiment(@PathVariable long id) {
        return ResponseEntity.of(_experimentService.getExperiment(id));
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
            return ResponseEntity.ok(_experimentService.createExperiment(createExperimentRequest, createdUserId));
        } catch (Exception e) {
            // TODO:: Logging
            String message = "Exception occurred creating experiment: " + e.getMessage();
            System.out.println(message);
            return ResponseEntity.internalServerError().body(message);
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
            return ResponseEntity.ok(_experimentService.updateExperiment(updateExperimentRequest));
        } catch (Exception e) {
            // TODO:: Logging
            String message = "Exception occurred updating experiment: " + e.getMessage();
            System.out.println(message);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /**
     * Creates a Sample in IN_PROGRESS state.
     *
     * @param experimentId The id of the experiment that the sample will belong to, used for validation and REST pathing.
     * @param createSampleRequest The details for creating the sample
     * @return The DTO for the created sample, or 500 with the error
     */
    @PostMapping("/{experimentId}/samples")
    public ResponseEntity<?> createSample(@PathVariable("experimentId") long experimentId, @RequestBody CreateSampleRequest createSampleRequest) {
        try {
            if (experimentId != createSampleRequest.experimentId()) {
                throw new ExperimentException("Experiment id between path and body do not match.");
            }
            return ResponseEntity.ok(_sampleService.createSample(createSampleRequest));
        } catch (Exception e) {
            // TODO:: Logging
            String message = "Exception occurred for experiment " + experimentId + " creating sample: " + e.getMessage();
            System.out.println(message);
            return ResponseEntity.internalServerError().body(message);
        }
    }

    /**
     * Updates an sample with the provided body, used for storing analyzed market data into market insights and modifying the state of the sample.
     *
     * @param experimentId The id of the experiment that the sample will belong to, used for validation and REST pathing.
     * @param updateSampleRequest The request body containing details to update the experiment with
     * @return The DTO of the updated sample, or 500 with the error
     */
    @PatchMapping("/{experimentId}/samples")
    @McpTool(description = "Updates the sample with the market insights and set sample state to DECIDING/COMPLETED/FAILED")
    public ResponseEntity<?> updateSample(@PathVariable("experimentId") long experimentId, @RequestBody UpdateSampleRequest updateSampleRequest) {
        try {
            return ResponseEntity.ok(_sampleService.updateSample(updateSampleRequest, experimentId));
        } catch (Exception e) {
            // TODO:: Logging
            String message = "Exception occurred for experiment " + experimentId + " updating sample: " + e.getMessage();
            System.out.println(message);
            return ResponseEntity.internalServerError().body(message);
        }
    }
}
