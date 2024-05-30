package com.vtd.backend.passkeys.models;

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registrations")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationStartResponse {
    @Id
    private String registrationId;
    private PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions;
    private String username; // Add username to link with the user
}
