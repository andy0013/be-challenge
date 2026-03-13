package com.challenge.bechallenge.profile.storage.presigner;

import com.challenge.bechallenge.profile.storage.S3Properties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
public class S3PresignAdapter {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public S3PresignAdapter(S3Presigner s3Presigner, S3Properties s3Properties) {
        this.s3Presigner = s3Presigner;
        this.s3Properties = s3Properties;
    }

    public String generateSignedUrl(String objectKey, String contentType) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(s3Properties.getSignedUrlExpirationSeconds()))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
}
