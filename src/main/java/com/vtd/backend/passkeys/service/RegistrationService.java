package com.vtd.backend.passkeys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vtd.backend.passkeys.utils.BytesUtil;
import com.vtd.backend.passkeys.entities.AccountEntity;
import com.vtd.backend.passkeys.models.RegistrationStartResponse;
import com.vtd.backend.passkeys.models.RegistrationFinishRequest;
import com.vtd.backend.passkeys.exception.CustomRegistrationFailedException;
import com.vtd.backend.passkeys.repository.AccountRepository;
import com.vtd.backend.passkeys.repository.RegistrationRepository;
import com.vtd.backend.passkeys.utils.WebAuthnUtils;
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
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private static final Long TIMEOUT = 1000L;
    private final AccountRepository accountRepository;
    private final RegistrationRepository registrationRepository;
    private final RelyingParty relyingParty;
    private final AppId appId;

    public RegistrationStartResponse startRegistration(String username) throws JsonProcessingException {
        Optional<AccountEntity> accountEntity = accountRepository.findByUsername(username);

        if (accountEntity.isEmpty()) {
            throw new IllegalArgumentException("Username not found");
        }

        AccountEntity account = accountEntity.get();
        byte[] accountIdBytes = BytesUtil.stringToBytes(account.getId());

        // Create a UserIdentity object
        UserIdentity userIdentity = UserIdentity.builder()
                .name(account.getUsername()) // Unique identifier for the user (e.g., username or user ID as a string)
                .displayName(account.getUsername()) // Display name for the user (can be the same as username or a more readable name)
                .id(new ByteArray(accountIdBytes)) // Unique identifier as a byte array
                .build();

        // Define authenticator selection criteria
        AuthenticatorSelectionCriteria authenticatorSelection = AuthenticatorSelectionCriteria.builder()
                .userVerification(UserVerificationRequirement.PREFERRED)
                .build();

        // Define the AppId extension
        RegistrationExtensionInputs extensions = RegistrationExtensionInputs.builder()
                .appidExclude(appId)
                .build();

        // Start the registration process
        StartRegistrationOptions startRegistrationOptions = StartRegistrationOptions.builder()
                .user(userIdentity)
                .timeout(TIMEOUT)
                .authenticatorSelection(authenticatorSelection)
                .extensions(extensions)
                .build();

        // Get the PublicKeyCredentialCreationOptions object
        PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = relyingParty
                .startRegistration(startRegistrationOptions);

        // Create a new registration response
        RegistrationStartResponse registrationStartResponse = new RegistrationStartResponse();
        registrationStartResponse.setRegistrationId(UUID.randomUUID().toString());
        registrationStartResponse.setUsername(username);
        registrationStartResponse.setPublicKeyCredentialCreationOptions(publicKeyCredentialCreationOptions);

        // Serialize to JSON
        String jsonString = publicKeyCredentialCreationOptions.toCredentialsCreateJson();
        System.out.println("Serialized JSON: " + jsonString);

        // Deserialize from JSON using the utility method
        PublicKeyCredentialCreationOptions deserializedOptions = WebAuthnUtils.deserializePublicKeyCredentialCreationOptions(jsonString);
        System.out.println("Deserialized Object: " + deserializedOptions);



        return registrationStartResponse;
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
