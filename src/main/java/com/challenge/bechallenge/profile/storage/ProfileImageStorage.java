package com.challenge.bechallenge.profile.storage;

import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;
import java.util.UUID;

public interface ProfileImageStorage {

	RequestSignedUrlResponse requestSignedUrl(UUID profileId, String fileName, String contentType);

	void validateBucketUrl(String imageUrl);
}