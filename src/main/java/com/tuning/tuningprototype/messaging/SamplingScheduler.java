package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.*;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class SamplingScheduler {

    private static final Logger log = LoggerFactory.getLogger(SamplingScheduler.class);

    @Value("${SAMPLING_SCHEDULER_QUEUE_ARN}")
    private String SAMPLING_SCHEDULER_QUEUE_ARN;

    @Value("${SCHEDULER_EXECUTION_ROLE_ARN}")
    private String SCHEDULER_EXECUTION_ROLE_ARN;

    private static final String SCHEDULE_NAME_PREFIX = "scheduled_sample.exp_id_%s.time_%s";
    private final SchedulerClient _schedulerClient;
    private final JsonMapper _jsonMapper;

    public SamplingScheduler(SchedulerClient schedulerClient, JsonMapper jsonMapper) {
        _schedulerClient = schedulerClient;
        _jsonMapper = jsonMapper;
    }

    public void scheduleSampleRun(CreateSampleRequest message) {
        // Define the exact execution timestamp using the 'at' expression (Format: YYYY-MM-DDTHH:MM:SS)
        String oneTimeScheduleExpression = String.format("at(%s)",
                LocalDateTime.ofInstant(Instant.ofEpochSecond(message.samplingTime()), ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Set target to SNS queue for scheduler
        // For local dev, this is currently mocked by LocalStack.
        Target target = Target.builder()
                .arn(SAMPLING_SCHEDULER_QUEUE_ARN)
                .roleArn(SCHEDULER_EXECUTION_ROLE_ARN)
                .input(_jsonMapper.writeValueAsString(message)) // JSON payload sent to target
                .build();
        // Build and send the schedule request
        CreateScheduleRequest request = CreateScheduleRequest.builder()
                .name(String.format(SCHEDULE_NAME_PREFIX, message.experimentId(), message.samplingTime()))
                .description(String.format("Scheduled sampling for experiment %s", message.experimentId()))
                .scheduleExpression(oneTimeScheduleExpression)
                .scheduleExpressionTimezone("UTC") // Set desired timezone evaluation
                .target(target)
                .flexibleTimeWindow(FlexibleTimeWindow.builder().mode(FlexibleTimeWindowMode.OFF).build())
                .actionAfterCompletion(ActionAfterCompletion.DELETE) // Automatically deletes schedule after invocation
                .build();

        CreateScheduleResponse response = _schedulerClient.createSchedule(request);
        log.info("Successfully created scheduled sample of ARN {} for {}", response.scheduleArn(), message);
        _schedulerClient.close();
    }
}

