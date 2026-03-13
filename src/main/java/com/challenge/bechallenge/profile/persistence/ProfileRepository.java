package com.challenge.bechallenge.profile.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<ProfileDocument, String> {
}