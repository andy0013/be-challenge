package com.challenge.bechallenge;

import com.challenge.bechallenge.profile.persistence.ProfileRepository;
import com.challenge.bechallenge.profile.service.ProfileService;
import com.challenge.bechallenge.profile.storage.ProfileImageStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeChallengeApplicationTests {

	@MockitoBean
	private ProfileRepository profileRepository;

	@MockitoBean
	private ProfileImageStorage profileImageStorage;

	@Autowired
	private ProfileService profileService;

	@Test
	void contextLoads() {
		assertNotNull(profileService);
		assertNotNull(profileRepository);
		assertNotNull(profileImageStorage);
	}
}