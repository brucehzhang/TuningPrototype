package com.tuning.tuningprototype.controllers;

import com.tuning.tuningprototype.models.ExperimentDto;
import com.tuning.tuningprototype.services.IExperimentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/experiments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExperimentController {

    private final IExperimentService _basicExperimentService;

    public ExperimentController(
            @Qualifier("basicExperimentService") IExperimentService basicExperimentService) {
        _basicExperimentService = basicExperimentService;
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
        return ResponseEntity.of(_basicExperimentService.getExperiment(id));
    }

}
