package com.challenge.bechallenge.profile.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.s3")
@Data
public class S3Properties {

	private String bucket;
	private String region;
	private String endpoint;
	private long signedUrlExpirationSeconds = 900;
}