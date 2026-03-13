package com.challenge.bechallenge.profile.storage;

import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;
import com.challenge.bechallenge.profile.exceptions.InvalidImageUrlException;
import com.challenge.bechallenge.profile.storage.presigner.S3PresignAdapter;
import com.challenge.bechallenge.profile.storage.url.LocalS3UrlStrategy;
import com.challenge.bechallenge.profile.storage.url.S3UrlStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class S3ProfileImageStorageTest {

    private S3UrlStrategy urlStrategy;
    private S3PresignAdapter presignAdapter;
    private S3ProfileImageStorage storage;

    private final UUID profileId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final String fileName = "my-file.png";

    @BeforeEach
    void setUp() {
        urlStrategy =  mock(LocalS3UrlStrategy.class);
        presignAdapter = mock(S3PresignAdapter.class);

        storage = new S3ProfileImageStorage(urlStrategy, presignAdapter,
                Set.of("image/jpeg", "image/png", "image/webp"));
    }

    @Test
    void givenValidRequest_whenRequestSignedUrl_thenShouldReturnSignedUrlResponse() {
        String contentType = "image/png";
        String expectedObjectKey = "profiles/" + profileId + "/" + fileName;

        when(presignAdapter.generateSignedUrl(expectedObjectKey, contentType))
                .thenReturn("http://signed-url.com/file");
        when(urlStrategy.buildFileUrl(expectedObjectKey))
                .thenReturn("http://cdn.com/file");

        RequestSignedUrlResponse response = storage.requestSignedUrl(profileId, fileName, contentType);

        assertEquals(expectedObjectKey, response.objectKey());
        assertEquals("http://signed-url.com/file", response.signedUrl());
        assertEquals("http://cdn.com/file", response.fileUrl());
    }

    @Test
    void givenInvalidContentType_whenRequestSignedUrl_thenShouldThrowException() {
        String invalidContentType = "application/pdf";

        InvalidImageUrlException ex = assertThrows(
                InvalidImageUrlException.class,
                () -> storage.requestSignedUrl(profileId, fileName, invalidContentType)
        );

        assertEquals("Unsupported image content type: " + invalidContentType, ex.getMessage());
    }

    @Test
    void givenInvalidUrlFormat_whenValidateBucketUrl_thenShouldThrowException() {
        String invalidUrl = "://invalid-url";

        InvalidImageUrlException ex = assertThrows(
                InvalidImageUrlException.class,
                () -> storage.validateBucketUrl(invalidUrl)
        );

        assertTrue(ex.getMessage().contains("Invalid image URL format"));
    }

    @Test
    void givenWrongHost_whenValidateBucketUrl_thenShouldThrowException() {
        String imageUrl = "https://wrong-bucket.s3.amazonaws.com/profiles/" + fileName;

        when(urlStrategy.getExpectedHost()).thenReturn("expected-bucket.s3.amazonaws.com");
        when(urlStrategy.normalizePathForValidation("/profiles/" + fileName)).thenReturn("/profiles/" + fileName);

        InvalidImageUrlException ex = assertThrows(
                InvalidImageUrlException.class,
                () -> storage.validateBucketUrl(imageUrl)
        );

        assertEquals("Image must belong to configured S3 bucket", ex.getMessage());
    }

    @Test
    void givenWrongPath_whenValidateBucketUrl_thenShouldThrowException() {
        String imageUrl = "https://expected-bucket.s3.amazonaws.com/not-profiles/" + fileName;

        when(urlStrategy.getExpectedHost()).thenReturn("expected-bucket.s3.amazonaws.com");
        when(urlStrategy.normalizePathForValidation("/not-profiles/" + fileName)).thenReturn("/not-profiles/" + fileName);

        InvalidImageUrlException ex = assertThrows(
                InvalidImageUrlException.class,
                () -> storage.validateBucketUrl(imageUrl)
        );

        assertEquals("Image must be inside profiles folder", ex.getMessage());
    }
}