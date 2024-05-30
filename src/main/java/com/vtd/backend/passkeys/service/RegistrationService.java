package com.vtd.backend.passkeys.service;

import com.vtd.backend.passkeys.BytesUtil;
import com.vtd.backend.passkeys.entities.AccountEntity;
import com.vtd.backend.passkeys.models.RegistrationStartResponse;
import com.vtd.backend.passkeys.models.RegistrationFinishRequest;
import com.vtd.backend.passkeys.exception.CustomRegistrationFailedException;
import com.vtd.backend.passkeys.repository.AccountRepository;
import com.vtd.backend.passkeys.repository.RegistrationRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.RegistrationExtensionInputs;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.extension.appid.AppId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final AccountRepository accountRepository;
    private final RegistrationRepository registrationRepository;
    private final RelyingParty relyingParty;
    private final AppId appId;

    public RegistrationStartResponse startRegistration(String username) {
        log.info("Starting registration for username: {}", username);
        Optional<AccountEntity> accountEntity = accountRepository.findByUsername(username);

        if (accountEntity.isEmpty()) {
            log.error("Username not found: {}", username);
            throw new IllegalArgumentException("Username not found");
        }

        log.info("Username found: {}", username);
        AccountEntity account = accountEntity.get();

        log.info("Creating registration for account: {}", account.getId());
        // Create a UserIdentity object
        UserIdentity userIdentity = UserIdentity.builder()
                .name(account.getUsername()) // Unique identifier for the user (e.g., username or user ID as a string)
                .displayName(account.getUsername()) // Display name for the user (can be the same as username or a more readable name)
                .id(new ByteArray(BytesUtil.longToBytes(Long.valueOf(account.getId())))) // Unique identifier as a byte array
                .build();
        log.info("User identity created: {}", userIdentity);

        log.info("Defining authenticator selection criteria");
        // Define authenticator selection criteria
        AuthenticatorSelectionCriteria authenticatorSelection = AuthenticatorSelectionCriteria.builder()
                .userVerification(UserVerificationRequirement.PREFERRED)
                .build();
        log.info("Authenticator selection criteria defined: {}", authenticatorSelection);

        log.info("Defining AppId extension");
        // Define the AppId extension
        RegistrationExtensionInputs extensions = RegistrationExtensionInputs.builder()
                .appidExclude(appId)
                .build();
        log.info("AppId extension defined: {}", extensions);

        log.info("Starting the registration process");
        // Start the registration process
        StartRegistrationOptions startRegistrationOptions = StartRegistrationOptions.builder()
                .user(userIdentity)
                .timeout(60000L)
                .authenticatorSelection(authenticatorSelection)
                .extensions(extensions)
                .build();
        log.info("Registration process started: {}", startRegistrationOptions);

        log.info("Getting the PublicKeyCredentialCreationOptions object");
        // Get the PublicKeyCredentialCreationOptions object
        PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = relyingParty
                .startRegistration(startRegistrationOptions);
        log.info("PublicKeyCredentialCreationOptions object created: {}", publicKeyCredentialCreationOptions);

        log.info("Creating a RegistrationStartResponse object");
        // Create a RegistrationStartResponse object
        RegistrationStartResponse registrationStartResponse = new RegistrationStartResponse();
        registrationStartResponse.setPublicKeyCredentialCreationOptions(publicKeyCredentialCreationOptions);
        log.info("RegistrationStartResponse object created: {}", registrationStartResponse);

        log.info("Saving the registration response");
        return registrationRepository.save(registrationStartResponse);
    }

    public void finishRegistration(RegistrationFinishRequest registrationFinishRequest) throws CustomRegistrationFailedException {
        Optional<RegistrationStartResponse> startResponseOptional = registrationRepository.findByRegistrationId(registrationFinishRequest.getRegistrationId());

        if (startResponseOptional.isEmpty()) {
            throw new CustomRegistrationFailedException("Registration ID not found");
        }

        RegistrationStartResponse startResponse = startResponseOptional.get();

        // Verify the challenge
        // TODO: FIX THIS
//        if (!startResponse.getPublicKeyCredentialCreationOptions().getChallenge().equals(ByteArray.fromBase64Url(registrationFinishRequest.getCredential().getResponse().getClientData().getChallenge()))) {
//            throw new CustomRegistrationFailedException("Challenge mismatch");
//        }

        // Your existing finish registration logic
    }
}
