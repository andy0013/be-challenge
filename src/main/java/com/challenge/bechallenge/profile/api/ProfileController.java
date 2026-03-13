package com.challenge.bechallenge.profile.api;

import com.challenge.bechallenge.profile.api.dto.CreateProfileRequest;
import com.challenge.bechallenge.profile.api.dto.ProfileResponse;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlRequest;
import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;
import com.challenge.bechallenge.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.net.URI;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@PostMapping("/signed-url")
	public RequestSignedUrlResponse requestSignedUrl(@Valid @RequestBody RequestSignedUrlRequest request) {
		return profileService.requestSignedUrl(request);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request) {
		return profileService.create(request);
	}
}