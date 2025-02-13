package br.com.fiap.postech.video_processor.infra.repositories.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class AwsSqsConfiguration {
    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    @Profile("!dev")
    public AwsCredentialsProvider awsCredentialsProvider() {
        return ContainerCredentialsProvider.builder().build();
    }

    @Bean
    @Profile("dev")
    public AwsCredentialsProvider awsCredentialsProviderDev() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
    }

    @Bean
    public SqsClient sqsClient(AwsCredentialsProvider credentialsProvider) {
        return SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
