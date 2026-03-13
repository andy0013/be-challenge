package com.challenge.bechallenge.profile.api.dto;

public record RequestSignedUrlResponse(
	String objectKey,
	String signedUrl,
	String fileUrl
) {
}
