package com.vtd.backend.passkeys.repository;

import com.vtd.backend.passkeys.models.RegistrationStartResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RegistrationRepository extends MongoRepository<RegistrationStartResponse, String> {
    Optional<RegistrationStartResponse> findByRegistrationId(String registrationId);
    Optional<RegistrationStartResponse> findByUsername(String username);
}
