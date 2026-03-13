package com.challenge.bechallenge.profile.storage.url;

import com.challenge.bechallenge.profile.storage.S3Properties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
public final class AwsS3UrlStrategy implements S3UrlStrategy {

    private static final String FILE_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";
    private static final String HOST_FORMAT = "%s.s3.%s.amazonaws.com";

    private final S3Properties properties;

    public AwsS3UrlStrategy(S3Properties properties) {
        this.properties = properties;
    }

    @Override
    public String buildFileUrl(String objectKey) {
        return FILE_URL_FORMAT.formatted(properties.getBucket(), properties.getRegion(), objectKey);
    }

    @Override
    public String getExpectedHost() {
        return HOST_FORMAT.formatted(properties.getBucket(), properties.getRegion());
    }

    @Override
    public String normalizePathForValidation(String path) {
        return path;
    }
}