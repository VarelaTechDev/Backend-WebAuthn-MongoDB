package com.vtd.backend.passkeys.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registrations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationStartResponse {
    @Id
    private String registrationId;
    private PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;
    private String username;
}
