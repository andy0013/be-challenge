package com.challenge.bechallenge.profile.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProfileRequest {

	@NotBlank
	private String name;

	@Size(max = 500)
	private String bio;

	@NotBlank
	private String imageUrl;
}