package com.vtd.backend.passkeys.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vtd.backend.passkeys.entities.AccountEntity;
import com.vtd.backend.passkeys.models.RegistrationStartResponse;
import com.vtd.backend.passkeys.repository.AccountRepository;
import com.vtd.backend.passkeys.repository.RegistrationRepository;
import com.yubico.webauthn.RelyingParty;
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
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class RegistrationServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private AppId appId;
    private RelyingParty relyingParty;
    private AccountEntity testAccount;

    @BeforeEach
    void setUp() throws InvalidAppIdException {
        // Initialize AppId
        appId = new AppId("https://localhost:8080");

        // Initialize RelyingParty
        Set<String> origins = Collections.singleton("http://localhost:8080");
        relyingParty = RelyingParty.builder()
                .identity(RelyingPartyIdentity.builder()
                        .id("localhost")
                        .name("Example Application")
                        .build())
                .credentialRepository(new MockCredentialRepository()) // Use mock CredentialRepository
                .origins(origins)
                .build();

        // Inject actual instances into the service
        registrationService = new RegistrationService(accountRepository, registrationRepository, relyingParty, appId);

        // Initialize test data
        testAccount = new AccountEntity();
        testAccount.setId(new ObjectId().toString()); // Use a valid ObjectId string
        testAccount.setUsername("test-username");
        UserIdentity userIdentity = UserIdentity.builder()
                .name("test-username")
                .displayName("test-username")
                .id(new ByteArray(testAccount.getId().getBytes())) // Use the ObjectId string
                .build();

        AuthenticatorSelectionCriteria authenticatorSelection = AuthenticatorSelectionCriteria.builder()
                .userVerification(UserVerificationRequirement.PREFERRED)
                .build();

        RegistrationExtensionInputs extensions = RegistrationExtensionInputs.builder()
                .appidExclude(appId)
                .build();

        PublicKeyCredentialCreationOptions testPublicKeyCredentialCreationOptions = PublicKeyCredentialCreationOptions.builder()
                .rp(RelyingPartyIdentity.builder().id("localhost").name("Example Application").build())
                .user(userIdentity)
                .challenge(new ByteArray("test-challenge".getBytes()))
                .pubKeyCredParams(List.of(
                        PublicKeyCredentialParameters.builder().alg(COSEAlgorithmIdentifier.ES256).type(PublicKeyCredentialType.PUBLIC_KEY).build()
                ))
                .timeout(1000L)
                .authenticatorSelection(authenticatorSelection)
                .extensions(extensions)
                .attestation(AttestationConveyancePreference.NONE)
                .build();
    }

    @Test
    void testStartRegistration() throws IOException {
        // Mock the behavior of accountRepository
        when(accountRepository.findByUsername("test-username")).thenReturn(Optional.of(testAccount));

        // Call the method under test
        RegistrationStartResponse response = registrationService.startRegistration("test-username");

        // Verify the response
        assertNotNull(response);
        assertEquals("test-username", response.getUsername());
        assertNotNull(response.getPublicKeyCredentialCreationOptions());

        // Pretty print JSON
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        String jsonString = writer.writeValueAsString(response);

        // Write JSON response to a file in the resource folder
        Path filePath = Path.of("src/test/resources/registrationStartResponse.json");
        Files.write(filePath, jsonString.getBytes());

        // Read the file and verify its contents
        File file = new File(filePath.toString());
        assertTrue(file.exists());

        // Read the contents of the file back into a String
        String fileContents = new String(Files.readAllBytes(filePath));

        // Compare the file contents with the original JSON String
        assertEquals(jsonString, fileContents);
    }
}