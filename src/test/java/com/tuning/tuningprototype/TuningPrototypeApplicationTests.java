package com.tuning.tuningprototype;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("No active Spring profile is set here, so the real RDS datasource/AWS Secrets Manager import/SQS listeners " +
        "are never configured for this to boot standalone. Out of scope for the unit test suite; covered service-by-service with Mockito instead.")
class TuningPrototypeApplicationTests {

    @Test
    void contextLoads() {
    }

}
