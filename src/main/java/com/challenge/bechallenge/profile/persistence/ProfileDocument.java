package com.challenge.bechallenge.profile.persistence;

import com.challenge.bechallenge.profile.api.dto.CreateProfileRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "profiles")
public record ProfileDocument(
	@Id String id,
	String name,
	String bio,
	String imageUrl
) {

	public static ProfileDocument create(UUID id, CreateProfileRequest r) {
		return new ProfileDocument(id.toString(), r.getName(), r.getBio(), r.getImageUrl());
	}
}