package com.vtd.backend.passkeys.repository;

import com.vtd.backend.passkeys.models.RegistrationChallenge;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegistrationChallengeRepository extends MongoRepository<RegistrationChallenge, String> {
    Optional<RegistrationChallenge> findByUsername(String username);
}
