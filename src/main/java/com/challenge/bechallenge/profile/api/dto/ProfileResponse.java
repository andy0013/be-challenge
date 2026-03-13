package com.challenge.bechallenge.profile.api.dto;

import java.util.UUID;

public record ProfileResponse(
	UUID id,
	String name,
	String bio,
	String imageUrl
) {
}