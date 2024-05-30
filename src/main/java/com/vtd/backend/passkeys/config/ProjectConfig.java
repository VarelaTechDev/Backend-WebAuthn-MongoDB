package com.vtd.backend.passkeys.config;

import com.vtd.backend.passkeys.service.CredentialService;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.extension.appid.AppId;
import com.yubico.webauthn.extension.appid.InvalidAppIdException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@ComponentScan(basePackages = "com.vtd.backend.passkeys")
@Configuration
public class ProjectConfig {

    public static final String ACCOUNT_ENDPOINT = "/account";
    public static final String REGISTER_ENDPOINT = "/register";

    @Value("${app.relying-party-id}")
    private String relyingParty;

    @Value("${app.relying-party-name}")
    private String relyingPartyName;

    @Value("${app.relying-party-origins}")
    private String relyingPartyOrigins;

    @Bean
    public RelyingParty relyingParty(CredentialService credentialService) {
        Set<String> origins;
        if (relyingPartyOrigins.contains(",")) {
            origins = Arrays.stream(relyingPartyOrigins.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        } else {
            origins = Collections.singleton(relyingPartyOrigins.trim());
        }

        return RelyingParty.builder()
                .identity(RelyingPartyIdentity.builder()
                        .id(relyingParty)
                        .name(relyingPartyName)
                        .build())
                .credentialRepository(credentialService)
                .origins(origins)
                .build();
    }

    @Bean
    public AppId appId() throws InvalidAppIdException {
        return new AppId("https://localhost:8080");
    }
}
