package com.tuning.tuningprototype.messaging;

import com.tuning.tuningprototype.models.requests.CreateSampleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleRequest;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleResponse;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SamplingSchedulerTest {

    private static final String QUEUE_ARN = "arn:aws:sqs:us-east-1:123456789012:sampling_scheduler_queue";
    private static final String ROLE_ARN = "arn:aws:iam::123456789012:role/scheduler-role";

    @Mock
    private SchedulerClient schedulerClient;
    @Mock
    private JsonMapper jsonMapper;

    private SamplingScheduler samplingScheduler;

    @BeforeEach
    void setUp() {
        samplingScheduler = new SamplingScheduler(schedulerClient, jsonMapper);
        ReflectionTestUtils.setField(samplingScheduler, "SAMPLING_SCHEDULER_QUEUE_ARN", QUEUE_ARN);
        ReflectionTestUtils.setField(samplingScheduler, "SCHEDULER_EXECUTION_ROLE_ARN", ROLE_ARN);
    }

    @Test
    void scheduleSampleRun_buildsRequestWithExpectedTargetAndClosesClient() {
        CreateSampleRequest message = new CreateSampleRequest(42L, null, 1_700_000_000L);
        when(jsonMapper.writeValueAsString(message)).thenReturn("{\"experimentId\":42}");
        CreateScheduleResponse response = CreateScheduleResponse.builder()
                .scheduleArn("arn:aws:scheduler:us-east-1:123456789012:schedule/default/scheduled_sample")
                .build();
        when(schedulerClient.createSchedule(org.mockito.ArgumentMatchers.any(CreateScheduleRequest.class)))
                .thenReturn(response);

        samplingScheduler.scheduleSampleRun(message);

        ArgumentCaptor<CreateScheduleRequest> captor = ArgumentCaptor.forClass(CreateScheduleRequest.class);
        verify(schedulerClient).createSchedule(captor.capture());
        CreateScheduleRequest request = captor.getValue();

        assertThat(request.name()).isEqualTo("scheduled_sample.exp_id_42.time_1700000000");
        assertThat(request.target().arn()).isEqualTo(QUEUE_ARN);
        assertThat(request.target().roleArn()).isEqualTo(ROLE_ARN);
        assertThat(request.target().input()).isEqualTo("{\"experimentId\":42}");
        assertThat(request.scheduleExpression()).startsWith("at(2023-11-14T22:13:20");
        assertThat(request.scheduleExpressionTimezone()).isEqualTo("UTC");

        verify(schedulerClient).close();
    }
}
