package com.challenge.bechallenge.profile.storage;

import com.challenge.bechallenge.profile.api.dto.RequestSignedUrlResponse;
import com.challenge.bechallenge.profile.exceptions.InvalidImageUrlException;
import com.challenge.bechallenge.profile.storage.presigner.S3PresignAdapter;
import com.challenge.bechallenge.profile.storage.url.S3UrlStrategy;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@Slf4j
public class S3ProfileImageStorage implements ProfileImageStorage {

	private static final String PROFILES_PREFIX = "profiles/";

	@Value("${s3.allowed-content-types:image/jpeg,image/png,image/webp}")
	private Set<String> allowedContentTypes;

	private final S3UrlStrategy urlStrategy;
	private final S3PresignAdapter presignAdapter;

	public S3ProfileImageStorage(S3UrlStrategy urlStrategy, S3PresignAdapter presignAdapter, Set<String> allowedContentTypes) {
		this.urlStrategy = urlStrategy;
		this.presignAdapter = presignAdapter;
		this.allowedContentTypes = allowedContentTypes;
	}

	@Override
	public RequestSignedUrlResponse requestSignedUrl(UUID profileId, String fileName, String contentType) {

		validateContentType(contentType);

		String objectKey = buildObjectKey(profileId, fileName);

		log.info("Generating signed URL for profileId={} fileName={}", profileId, fileName);
		String signedUrl = presignAdapter.generateSignedUrl(objectKey, contentType);

		String fileUrl = urlStrategy.buildFileUrl(objectKey);

		return new RequestSignedUrlResponse(objectKey, signedUrl, fileUrl);
	}

	@Override
	public void validateBucketUrl(String imageUrl) {
		log.info("Validating bucket URL: {}", imageUrl);
		URI uri = parseUri(imageUrl);
		String host = uri.getHost();
		String path = urlStrategy.normalizePathForValidation(uri.getPath());

		validateHost(host, imageUrl);
		validatePath(path, imageUrl);
		log.info("Bucket URL validated successfully: {}", imageUrl);
	}

	private String buildObjectKey(UUID profileId, String fileName) {
		String safeFileName = Paths.get(fileName).getFileName().toString();
		return formatObjectKey(profileId, safeFileName);
	}

	private void validateContentType(String contentType) {
		if (!allowedContentTypes.contains(contentType)) {
			throw new InvalidImageUrlException("Unsupported image content type: " + contentType);
		}
	}

	private URI parseUri(String imageUrl) {
		try {
			return URI.create(imageUrl);
		} catch (Exception e) {
			throw new InvalidImageUrlException("Invalid image URL format: " + imageUrl);
		}
	}

	private void validateHost(String host, String imageUrl) {
		String expectedHost = urlStrategy.getExpectedHost();
		if (host == null || !host.equals(expectedHost)) {
			log.warn("Invalid host for image URL {}: expected {}, got {}", imageUrl, expectedHost, host);
			throw new InvalidImageUrlException("Image must belong to configured S3 bucket");
		}
	}

	private void validatePath(String path, String imageUrl) {
		String expectedPrefix = "/".concat(PROFILES_PREFIX);
		if (path == null || !path.startsWith(expectedPrefix)) {
			log.warn("Invalid path for image URL {}: expected prefix {}", imageUrl, expectedPrefix);
			throw new InvalidImageUrlException("Image must be inside profiles folder");
		}
	}

	private static String formatObjectKey(UUID profileId, String safeFileName) {
		return "%s%s/%s".formatted(PROFILES_PREFIX, profileId, safeFileName);
	}
}