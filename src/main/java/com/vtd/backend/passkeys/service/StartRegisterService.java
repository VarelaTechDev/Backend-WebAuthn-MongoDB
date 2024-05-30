package com.vtd.backend.passkeys.service;

import com.vtd.backend.passkeys.models.RegistrationStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartRegisterService {

    private static final long TIMEOUT_MS = 60000L;
    private final CredentialService credentialService;

    public RegistrationStartResponse startRegistration(String username) {
        return null;
    }
}
