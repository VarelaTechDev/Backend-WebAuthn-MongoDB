package com.vtd.backend.passkeys.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasskeyEntity {

    private String id; // MongoDB will generate this ID
    private String credentialId; // Base64 encoded byte[]
    private Long count; // The counter
    private byte[] publicKey; // Public key stored as a byte array

    public PasskeyEntity(String credentialId, Long count, byte[] publicKey) {
        this.credentialId = credentialId;
        this.count = count;
        this.publicKey = publicKey;
    }
}
