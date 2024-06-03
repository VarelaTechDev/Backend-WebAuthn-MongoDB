package com.vtd.backend.passkeys.repository;

import com.vtd.backend.passkeys.models.RegistrationChallenge;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
@DataMongoTest // Sets up an embedded MongoDB instance
@ExtendWith(SpringExtension.class)
public class RegistrationChallengeRepositoryIntegrationTest {

    @Autowired
    private RegistrationChallengeRepository registrationChallengeRepository;



    @Test
    public void testFindByUsername() {
        RegistrationChallenge challenge = new RegistrationChallenge();
        challenge.setRegistrationId("testRegId");
        challenge.setUsername("testUser");
        challenge.setPublicKeyCredentialCreationOptionsJson("{}");
        challenge.setCreatedAt(new Date());
        challenge.setExpiresAt(new Date(System.currentTimeMillis() + 10000));
        registrationChallengeRepository.save(challenge);

        Optional<RegistrationChallenge> foundChallenge = registrationChallengeRepository.findByUsername("testUser");

        assertThat(foundChallenge).isPresent();
        assertThat(foundChallenge.get().getUsername()).isEqualTo("testUser");
    }

    @Test
    public void testFindByRegistrationId() {
        RegistrationChallenge challenge = new RegistrationChallenge();
        challenge.setRegistrationId("testRegId");
        challenge.setUsername("testUser");
        challenge.setPublicKeyCredentialCreationOptionsJson("{}");
        challenge.setCreatedAt(new Date());
        challenge.setExpiresAt(new Date(System.currentTimeMillis() + 10000));
        registrationChallengeRepository.save(challenge);

        Optional<RegistrationChallenge> foundChallenge = registrationChallengeRepository.findByRegistrationId("testRegId");

        assertThat(foundChallenge).isPresent();
        assertThat(foundChallenge.get().getRegistrationId()).isEqualTo("testRegId");
    }
}
