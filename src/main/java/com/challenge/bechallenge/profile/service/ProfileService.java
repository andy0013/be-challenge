package com.challenge.bechallenge.profile.service;

import com.challenge.bechallenge.profile.api.dto.CreateProfileRequest;
import com.challenge.bechallenge.profile.api.dto.ProfileResponse;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlRequest;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;

public interface ProfileService {

	RequestSignedUrlResponse requestSignedUrl(RequestSignedUrlRequest request);

	ProfileResponse create(CreateProfileRequest request);
}