package com.challenge.bechallenge.profile.api;


import com.challenge.bechallenge.profile.exceptions.InvalidImageUrlException;
import org.junit.jupiter.api.Test;

import com.challenge.bechallenge.profile.api.dto.*;
import com.challenge.bechallenge.profile.service.ProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerEndpointTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProfileService profileService;

	@Test
	void givenValidSignedUrlRequest_whenRequestSignedUrl_thenShouldReturnSignedUrlResponse() throws Exception {
		RequestSignedUrlRequest request = new RequestSignedUrlRequest();
		request.setFileName("my-file.jpeg");
		request.setContentType("image/jpeg");

		RequestSignedUrlResponse mockResponse = new RequestSignedUrlResponse(
				"my-file.jpeg",
				"http://signed-url.com/file",
				"http://cdn.com/file"
		);

		when(profileService.requestSignedUrl(any(RequestSignedUrlRequest.class))).thenReturn(mockResponse);

		mockMvc.perform(post("/api/profiles/signed-url")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.objectKey").value("my-file.jpeg"))
				.andExpect(jsonPath("$.signedUrl").value("http://signed-url.com/file"))
				.andExpect(jsonPath("$.fileUrl").value("http://cdn.com/file"));
	}

	@Test
	void givenValidCreateProfileRequest_whenCreateProfile_thenShouldReturnProfileResponse() throws Exception {
		CreateProfileRequest request = new CreateProfileRequest();
		request.setName("John Doe");
		request.setBio("Software engineer");
		request.setImageUrl("http://images.com/john.jpg");

		ProfileResponse mockResponse = new ProfileResponse(
				UUID.fromString("11111111-1111-1111-1111-111111111111"),
				"John Doe",
				"Software engineer",
				"http://images.com/john.jpg"
		);

		when(profileService.create(any(CreateProfileRequest.class))).thenReturn(mockResponse);

		mockMvc.perform(post("/api/profiles")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value("11111111-1111-1111-1111-111111111111"))
				.andExpect(jsonPath("$.name").value("John Doe"))
				.andExpect(jsonPath("$.bio").value("Software engineer"))
				.andExpect(jsonPath("$.imageUrl").value("http://images.com/john.jpg"));
	}

	@Test
	void givenInvalidSignedUrlRequest_whenRequestSignedUrl_thenShouldReturnBadRequest() throws Exception {
		RequestSignedUrlRequest invalidRequest = new RequestSignedUrlRequest();
		invalidRequest.setFileName("");
		invalidRequest.setContentType("");
		mockMvc.perform(post("/api/profiles/signed-url")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void givenInvalidCreateProfileRequest_whenCreateProfile_thenShouldReturnBadRequest() throws Exception {
		CreateProfileRequest invalidRequest = new CreateProfileRequest();
		invalidRequest.setName("");
		invalidRequest.setBio("A".repeat(600));
		invalidRequest.setImageUrl("");

		mockMvc.perform(post("/api/profiles")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void givenInvalidImageUrl_whenCreateProfile_thenShouldReturnCustomErrorResponse() throws Exception {
		CreateProfileRequest request = new CreateProfileRequest();
		request.setName("John Doe");
		request.setBio("Software engineer");
		request.setImageUrl("invalid-url");

		when(profileService.create(any(CreateProfileRequest.class)))
				.thenThrow(new InvalidImageUrlException("Invalid image URL"));

		mockMvc.perform(post("/api/profiles")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid image URL"));
	}
}