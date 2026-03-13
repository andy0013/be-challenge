package com.challenge.bechallenge.profile.storage.presigner;

import com.challenge.bechallenge.profile.storage.S3Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3PresignAdapterTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties s3Properties;

    private S3PresignAdapter adapter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        adapter = new S3PresignAdapter(s3Presigner, s3Properties);
    }

    @Test
    void givenValidInput_whenGenerateSignedUrl_thenReturnsExpectedUrl() throws Exception {
        String bucket = "my-bucket";
        String objectKey = "profiles/test.jpg";
        String contentType = "image/jpeg";
        long expirationSeconds = 300;
        String expectedUrl = "https://my-bucket.s3.amazonaws.com/" + objectKey;

        when(s3Properties.getBucket()).thenReturn(bucket);
        when(s3Properties.getSignedUrlExpirationSeconds()).thenReturn(expirationSeconds);

        PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        String signedUrl = adapter.generateSignedUrl(objectKey, contentType);

        assertEquals(expectedUrl, signedUrl);

        ArgumentCaptor<PutObjectPresignRequest> captor = ArgumentCaptor.forClass(PutObjectPresignRequest.class);
        verify(s3Presigner).presignPutObject(captor.capture());

        PutObjectPresignRequest actual = captor.getValue();
        PutObjectRequest putReq = actual.putObjectRequest();

        assertEquals(bucket, putReq.bucket());
        assertEquals(objectKey, putReq.key());
        assertEquals(contentType, putReq.contentType());
        assertEquals(Duration.ofSeconds(expirationSeconds), actual.signatureDuration());
    }

    @Test
    void givenPresignerThrowsException_whenGenerateSignedUrl_thenPropagatesException() {
        String objectKey = "profiles/test.jpg";
        String contentType = "image/jpeg";

        when(s3Properties.getBucket()).thenReturn("my-bucket");
        when(s3Properties.getSignedUrlExpirationSeconds()).thenReturn(300L);

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenThrow(new RuntimeException("Presign failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adapter.generateSignedUrl(objectKey, contentType));

        assertEquals("Presign failed", ex.getMessage());
    }
}