package com.vtd.backend.passkeys.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "registrationChallenges")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@CompoundIndex(def = "{'username': 1}", unique = true)
public class RegistrationChallenge {
    @Id
    private String id; // MongoDB's unique identifier

    private String registrationId; // Our business logic identifier
    private String publicKeyCredentialCreationOptionsJson;
    private String username;

    private Date createdAt;

    @Indexed(expireAfterSeconds = 0)
    private Date expiresAt;
}
