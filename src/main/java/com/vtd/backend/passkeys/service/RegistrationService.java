package com.vtd.backend.passkeys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vtd.backend.passkeys.utils.BytesUtil;
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
import com.yubico.webauthn.data.COSEAlgorithmIdentifier;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.data.RegistrationExtensionInputs;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.extension.appid.AppId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private static final Long TIMEOUT = 20000L;
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
                // .authenticatorAttachment(AuthenticatorAttachment.PLATFORM) // Specifies the type of authenticator to use:
                // Options:
                // - AuthenticatorAttachment.PLATFORM: Uses a platform authenticator (built into the device, e.g., Touch ID, Windows Hello).
                // - AuthenticatorAttachment.CROSS_PLATFORM: Uses a cross-platform authenticator (external devices like USB security keys).
                // - Omit this setting to allow both platform and cross-platform authenticators.

                // .userVerification(UserVerificationRequirement.PREFERRED) // Specifies the user verification requirement:
                // Options:
                // - UserVerificationRequirement.PREFERRED: Prefers user verification (biometrics, PIN), but allows registration without it if not available.
                // - UserVerificationRequirement.DISCOURAGED: Suggests not using user verification, but does not prohibit it.
                // - UserVerificationRequirement.REQUIRED: Enforces user verification; registration will fail if the authenticator cannot perform it.
                .userVerification(UserVerificationRequirement.PREFERRED)
                .build();

        // Define the AppId extension
        RegistrationExtensionInputs extensions = RegistrationExtensionInputs.builder()
                .appidExclude(appId) // Specifies the AppID exclusion extension:
                // The AppID extension is used to support legacy FIDO U2F authenticators.

                .credProps(true) // Specifies the credProps extension:
                // Indicates that the client should return information about the credential's properties.

                //.largeBlob()
                //.uvm()
                .build();

        // Define public key credential parameters
        // Included Algorithms:
        // - ES256 (ECDSA with P-256 and SHA-256): Widely used, good balance of security and performance, recommended by NIST.
        // - ES384 (ECDSA with P-384 and SHA-384): Higher security than ES256, suitable for environments with higher security requirements.
        // - ES512 (ECDSA with P-521 and SHA-512): Highest level of security among the ECDSA algorithms.
        // - RS256 (RSASSA-PKCS1-v1_5 with SHA-256): Commonly used, strong security, suitable for compatibility with older systems.
        // - RS384 (RSASSA-PKCS1-v1_5 with SHA-384): Higher security than RS256.
        // - RS512 (RSASSA-PKCS1-v1_5 with SHA-512): Highest level of security among the RSA algorithms.
        //
        // Excluded Algorithms:
        // - EdDSA (Edwards-curve Digital Signature Algorithm): Not included due to its relatively recent adoption and potential compatibility issues in some enterprise environments.
        // - RS1 (RSASSA-PKCS1-v1_5 with SHA-1): Not included due to known security weaknesses in SHA-1.
        // Define public key credential parameters
        List<PublicKeyCredentialParameters> pubKeyCredParams = List.of(
                PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.ES256)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build(),
                PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.ES384)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build(),
                PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.ES512)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build(),
                PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.RS256)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build(),
                PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.RS384)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build(),
                PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.RS512)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build()
        );

        // Start the registration process
        StartRegistrationOptions startRegistrationOptions = StartRegistrationOptions.builder()
                .user(userIdentity)
                .timeout(TIMEOUT) // Specifies the timeout for the registration operation:
                // Options:
                // - TIMEOUT: The amount of time the operation is allowed to take, in milliseconds.
                .authenticatorSelection(authenticatorSelection)
                .extensions(extensions)
                .build();

        // Get the PublicKeyCredentialCreationOptions object
        PublicKeyCredentialCreationOptions publicKeyCredentialCreationOptions = relyingParty
                .startRegistration(startRegistrationOptions)
                .toBuilder()
                .pubKeyCredParams(pubKeyCredParams)
                .build();

        // Create a new registration response
        RegistrationStartResponse registrationStartResponse = new RegistrationStartResponse();
        registrationStartResponse.setRegistrationId(UUID.randomUUID().toString());
        registrationStartResponse.setUsername(username);
        registrationStartResponse.setPublicKeyCredentialCreationOptions(publicKeyCredentialCreationOptions);

//        // Serialize to JSON
//        String jsonString = publicKeyCredentialCreationOptions.toCredentialsCreateJson();
//        System.out.println("Serialized JSON: " + jsonString);
//
//        // Deserialize from JSON using the utility method
//        PublicKeyCredentialCreationOptions deserializedOptions = WebAuthnUtils.deserializePublicKeyCredentialCreationOptions(jsonString);
//        System.out.println("Deserialized Object: " + deserializedOptions);

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
