package com.vtd.backend.passkeys.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

public class WebAuthnUtils {

    public static PublicKeyCredentialCreationOptions deserializePublicKeyCredentialCreationOptions(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString);
        JsonNode publicKeyNode = rootNode.path("publicKey");
        String publicKeyJsonString = mapper.writeValueAsString(publicKeyNode);
        return PublicKeyCredentialCreationOptions.fromJson(publicKeyJsonString);
    }
}