package com.challenge.bechallenge.profile.service;

import com.challenge.bechallenge.profile.api.dto.CreateProfileRequest;
import com.challenge.bechallenge.profile.api.dto.ProfileResponse;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlRequest;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;
import com.challenge.bechallenge.profile.persistence.ProfileDocument;
import com.challenge.bechallenge.profile.persistence.ProfileRepository;
import com.challenge.bechallenge.profile.storage.ProfileImageStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

	private final ProfileRepository profileRepository;
	private final ProfileImageStorage profileImageStorage;

	public ProfileServiceImpl(ProfileRepository profileRepository,
							  ProfileImageStorage profileImageStorage) {
		this.profileRepository = profileRepository;
		this.profileImageStorage = profileImageStorage;
	}

	@Override
	public RequestSignedUrlResponse requestSignedUrl(RequestSignedUrlRequest request) {
		UUID profileId = UUID.randomUUID();
		log.info("Requesting signed URL for profileId={}, fileName={}, contentType={}",
				profileId, request.getFileName(), request.getContentType());

		return profileImageStorage.requestSignedUrl(
				profileId,
				request.getFileName(),
				request.getContentType()
		);
	}

	@Override
	public ProfileResponse create(CreateProfileRequest request) {
		log.info("Creating profile with name={}, imageUrl={}", request.getName(), request.getImageUrl());
		profileImageStorage.validateBucketUrl(request.getImageUrl());

		UUID id = UUID.randomUUID();

		ProfileDocument saved = profileRepository.save(
				ProfileDocument.create(id, request)
		);
		log.info("Profile creation completed successfully: id={}", id);
		return new ProfileResponse(
				UUID.fromString(saved.id()), saved.name(), saved.bio(), saved.imageUrl());
	}
}