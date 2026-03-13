package com.challenge.bechallenge.profile.storage.url;

public sealed interface S3UrlStrategy permits LocalS3UrlStrategy, AwsS3UrlStrategy {
    String buildFileUrl(String objectKey);
    String getExpectedHost();
    String normalizePathForValidation(String path);
}
