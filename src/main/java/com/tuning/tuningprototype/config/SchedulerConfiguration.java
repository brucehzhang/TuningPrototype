package com.tuning.tuningprototype.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.SchedulerClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class SchedulerConfiguration {

//    @Value("${spring.cloud.aws.endpoint}")
//    private String endpointOverride;

    @Bean
    public SchedulerClient getSchedulerClient(AwsCredentialsProvider awsCredentialsProvider) throws URISyntaxException {
        SchedulerClientBuilder builder = SchedulerClient.builder()
                .credentialsProvider(awsCredentialsProvider);
//        // LocalStack override only for locally running server
//        if (endpointOverride.contains("localhost")) {
//            builder.endpointOverride(new URI(endpointOverride));
//        }
        return builder.build();
    }
}
