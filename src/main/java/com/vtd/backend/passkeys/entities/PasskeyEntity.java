package com.vtd.backend.passkeys.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasskeyEntity {

    private String id;

    // Base64 encoded byte[]
    private String credentialId;
    private Long count;
    private byte[] publicKey;

    public PasskeyEntity(String credentialId, Long count, byte[] publicKey) {
        this.credentialId = credentialId;
        this.count = count;
        this.publicKey = publicKey;
    }
}
