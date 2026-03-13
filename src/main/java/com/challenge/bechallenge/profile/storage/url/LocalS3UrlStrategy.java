package com.challenge.bechallenge.profile.storage.url;

import com.challenge.bechallenge.profile.storage.S3Properties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@Profile("local")
public final class LocalS3UrlStrategy implements S3UrlStrategy {

    private final S3Properties properties;

    public LocalS3UrlStrategy(S3Properties properties) {
        this.properties = properties;
    }

    @Override
    public String buildFileUrl(String objectKey) {
        return "%s/%s/%s".formatted(properties.getEndpoint(), properties.getBucket(), objectKey);
    }

    @Override
    public String getExpectedHost() {
        return URI.create(properties.getEndpoint()).getHost();
    }

    @Override
    public String normalizePathForValidation(String path) {
        int idx = path.indexOf(properties.getBucket());
        return (idx != -1) ? path.substring(idx + properties.getBucket().length()) : path;
    }
}
