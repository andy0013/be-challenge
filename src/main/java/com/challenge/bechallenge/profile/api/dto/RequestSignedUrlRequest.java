package com.challenge.bechallenge.profile.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestSignedUrlRequest {

	@NotBlank
	private String fileName;

	@NotBlank
	private String contentType;
}
