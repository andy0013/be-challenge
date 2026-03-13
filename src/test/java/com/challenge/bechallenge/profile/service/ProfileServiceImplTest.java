package com.challenge.bechallenge.profile.service;

import com.challenge.bechallenge.profile.api.dto.CreateProfileRequest;
import com.challenge.bechallenge.profile.api.dto.ProfileResponse;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlRequest;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;
import com.challenge.bechallenge.profile.exceptions.InvalidImageUrlException;
import com.challenge.bechallenge.profile.persistence.ProfileDocument;
import com.challenge.bechallenge.profile.persistence.ProfileRepository;
import com.challenge.bechallenge.profile.storage.ProfileImageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    private ProfileRepository profileRepository;
    private ProfileImageStorage profileImageStorage;
    private ProfileServiceImpl profileService;

    @BeforeEach
    void setUp() {
        profileRepository = mock(ProfileRepository.class);
        profileImageStorage = mock(ProfileImageStorage.class);
        profileService = new ProfileServiceImpl(profileRepository, profileImageStorage);
    }

    @Test
    void givenValidRequest_whenRequestSignedUrl_thenShouldReturnSignedUrlResponse() {
        RequestSignedUrlRequest request = new RequestSignedUrlRequest();
        request.setFileName("my-file.png");
        request.setContentType("image/png");

        RequestSignedUrlResponse mockResponse = new RequestSignedUrlResponse(
                "profiles/uuid/my-file.png",
                "http://signed-url.com/file",
                "http://cdn.com/file"
        );

        when(profileImageStorage.requestSignedUrl(any(UUID.class), eq("my-file.png"), eq("image/png")))
                .thenReturn(mockResponse);

        RequestSignedUrlResponse response = profileService.requestSignedUrl(request);

        assertEquals(mockResponse.objectKey(), response.objectKey());
        assertEquals(mockResponse.signedUrl(), response.signedUrl());
        assertEquals(mockResponse.fileUrl(), response.fileUrl());
    }

    @Test
    void givenValidCreateProfileRequest_whenCreateProfile_thenShouldValidateAndSaveProfile() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("John Doe");
        request.setBio("Software engineer");
        request.setImageUrl("http://bucket.s3.amazonaws.com/profiles/john.png");

        doNothing().when(profileImageStorage).validateBucketUrl(request.getImageUrl());

        ArgumentCaptor<ProfileDocument> captor = ArgumentCaptor.forClass(ProfileDocument.class);

        when(profileRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProfileResponse response = profileService.create(request);

        ProfileDocument savedDoc = captor.getValue();
        assertEquals(savedDoc.id(), response.id().toString());
        assertEquals(savedDoc.name(), response.name());
        assertEquals(savedDoc.bio(), response.bio());
        assertEquals(savedDoc.imageUrl(), response.imageUrl());

        verify(profileImageStorage).validateBucketUrl(request.getImageUrl());
        verify(profileRepository).save(any(ProfileDocument.class));
    }

    @Test
    void givenInvalidImageUrl_whenCreateProfile_thenShouldThrowException() {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName("John Doe");
        request.setBio("Software engineer");
        request.setImageUrl("invalid-url");

        doThrow(new InvalidImageUrlException("Invalid image URL"))
                .when(profileImageStorage).validateBucketUrl(request.getImageUrl());

        InvalidImageUrlException ex = assertThrows(
                InvalidImageUrlException.class,
                () -> profileService.create(request)
        );

        assertEquals("Invalid image URL", ex.getMessage());

        verify(profileRepository, never()).save(any());
    }
}