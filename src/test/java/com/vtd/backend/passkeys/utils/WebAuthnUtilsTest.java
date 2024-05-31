package com.vtd.backend.passkeys.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.data.AttestationConveyancePreference;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.COSEAlgorithmIdentifier;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialParameters;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import com.yubico.webauthn.data.RegistrationExtensionInputs;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.extension.appid.AppId;
import com.yubico.webauthn.extension.appid.InvalidAppIdException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for deserializing PublicKeyCredentialCreationOptions using {@link WebAuthnUtils}.
 */
class WebAuthnUtilsTest {

    /**
     * Create the AppId instance statically.
     * This ensures the AppId is initialized once and can be used throughout the test class.
     */
    private static final AppId appId;

    static {
        try {
            appId = new AppId("https://localhost:8080");
        } catch (InvalidAppIdException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Test method for deserializing PublicKeyCredentialCreationOptions.
     *
     * <p>This method creates a PublicKeyCredentialCreationOptions instance using builders,
     * serializes it to JSON, and then deserializes it back to an object using the
     * WebAuthnUtils.deserializePublicKeyCredentialCreationOptions method.
     * The test verifies that the deserialized object matches the original.</p>
     *
     * @throws JsonProcessingException if JSON processing fails.
     */
    @Test
    void testDeserializePublicKeyCredentialCreationOptions() throws JsonProcessingException {
        // Create a PublicKeyCredentialCreationOptions instance for testing using builders
        PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.builder()
                .rp(RelyingPartyIdentity.builder()
                        .id("localhost")
                        .name("Example Application")
                        .build())
                .user(UserIdentity.builder()
                        .name("a1")
                        .displayName("a1")
                        .id(new ByteArray("ZljP-9VCSBBevs5j".getBytes()))
                        .build())
                .challenge(new ByteArray("Oiczyd2SIZX0IjuQeMcEN771HDO_IG4cubup0RDO_eU".getBytes()))
                .pubKeyCredParams(List.of(
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.EdDSA).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES256).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES384).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES512).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS256).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS384).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS512).type(PublicKeyCredentialType.PUBLIC_KEY).build(),
                    PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.RS1).type(PublicKeyCredentialType.PUBLIC_KEY).build()))
                .timeout(1000L)
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder().userVerification(UserVerificationRequirement.PREFERRED).build())
                .attestation(AttestationConveyancePreference.NONE)
                .extensions(RegistrationExtensionInputs.builder()
                        .appidExclude(appId)
                        .build())
                .build();

        // Serialize the object to JSON
        String jsonString = options.toCredentialsCreateJson();

        // Use the utility method to deserialize it
        PublicKeyCredentialCreationOptions deserializedOptions = WebAuthnUtils.deserializePublicKeyCredentialCreationOptions(jsonString);

        // Verify the deserialized object matches the original
        assertAll("Deserialized PublicKeyCredentialCreationOptions",
                () -> assertNotNull(deserializedOptions),
                () -> assertEquals(options.getRp(), deserializedOptions.getRp()),
                () -> assertEquals(options.getUser(), deserializedOptions.getUser()),
                () -> assertEquals(options.getChallenge(), deserializedOptions.getChallenge()),
                () -> assertEquals(options.getPubKeyCredParams(), deserializedOptions.getPubKeyCredParams()),
                () -> assertEquals(options.getTimeout(), deserializedOptions.getTimeout()),
                () -> assertEquals(options.getAuthenticatorSelection(), deserializedOptions.getAuthenticatorSelection()),
                () -> assertEquals(options.getAttestation(), deserializedOptions.getAttestation()),
                () -> assertEquals(options.getExtensions(), deserializedOptions.getExtensions())
        );
    }
}
