package com.vtd.backend.passkeys.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordEntity {

    /**
     * Encrypted password
     */
    private String encryptedPassword;

    public PasswordEntity(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
