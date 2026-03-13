package com.challenge.bechallenge.profile.configuration;

import com.challenge.bechallenge.profile.storage.S3Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.util.Objects;

@Configuration
public class S3Config {

    private final S3Properties properties;

    public S3Config(S3Properties properties) {
        this.properties = properties;
    }

    @Bean
    @Profile("local")
    public S3Presigner localS3Presigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(properties.getRegion()));

        if (Objects.nonNull(properties.getEndpoint())) {
            builder.endpointOverride(URI.create(properties.getEndpoint()));
        }

        builder.serviceConfiguration(
                S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
        );

        return builder.build();
    }

    @Bean
    @Profile("!local")
    public S3Presigner awsS3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .build();
    }
}