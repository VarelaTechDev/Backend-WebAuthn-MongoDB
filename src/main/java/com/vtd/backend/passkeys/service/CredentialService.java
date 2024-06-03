package com.vtd.backend.passkeys.service;

import com.vtd.backend.passkeys.entities.AccountEntity;
import com.vtd.backend.passkeys.entities.PasskeyEntity;
import com.vtd.backend.passkeys.repository.AccountRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.PublicKeyCredentialType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
@Getter
@Setter
@RequiredArgsConstructor
public class CredentialService implements CredentialRepository {

    @Autowired
    private AccountRepository accountRepository;

    private final MongoTemplate mongoTemplate;



    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        Optional<AccountEntity> accountOptional = accountRepository.findByUsername(username);

        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Username not found: " + username);
        }

        AccountEntity account = accountOptional.get();
        System.out.println("Size is: " + account.getPasskeys().size());

        for (PasskeyEntity passkey : account.getPasskeys()) {
            System.out.println("Processing passkey: " + passkey.getCredentialId());
            System.out.println("Passkey public key: " + new ByteArray(passkey.getPublicKey()).getBase64());
            System.out.println("Passkey count: " + passkey.getCount());
        }



        return account.getPasskeys()
                .stream()
                .map(passkey -> PublicKeyCredentialDescriptor.builder()
                .id(new ByteArray(Base64.getDecoder().decode(passkey.getCredentialId())))
                .type(PublicKeyCredentialType.PUBLIC_KEY)
                .transports(Collections.emptySet())
                .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {

        return Optional.empty();
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        System.out.println("INVOKED: LOOKUP ALL WAS INVOKED");

        // Convert ByteArray to Base64 encoded string
        String credentialIdBase64 = Base64.getEncoder().encodeToString(credentialId.getBytes());
        System.out.println("CREDENTIAL ID BASE64: " + credentialIdBase64);

        // Fetch all accounts
        System.out.println("BEFORE FIND ALL");
        List<AccountEntity> accounts = accountRepository.findAll();
        System.out.println("AFTER FIND ALL");

        for (AccountEntity account : accounts) {
            System.out.println("Account size: " + accounts.size());
            System.out.println("INSIDE ACCOUNT FOR LOOP");

            // Initialize passkeys if null
            if (account.getPasskeys() == null) {
                account.setPasskeys(new ArrayList<>());
            }

            System.out.println("BEFORE ENTERING PASSKEY FOR LOOP");
            System.out.println("PASSKEY SIZE: " + account.getPasskeys().size());
            for (PasskeyEntity passkey : account.getPasskeys()) {
                System.out.println("INSIDE PASSKEY FOR LOOP");

                if (passkey.getCredentialId().equals(credentialIdBase64)) {
                    System.out.println("CREDENTIAL ID MATCHED");
                    RegisteredCredential registeredCredential = RegisteredCredential.builder()
                            .credentialId(credentialId)
                            .userHandle(new ByteArray(account.getAccountId().getBytes()))
                            .publicKeyCose(new ByteArray(passkey.getPublicKey()))
                            .signatureCount(passkey.getCount())
                            .build();
                    return Set.of(registeredCredential);
                }
                System.out.println("NO MATCH");
            }
            System.out.println("OUTSIDE PASSKEY FOR LOOP");
        }
        System.out.println("OUTSIDE ACCOUNT FOR LOOP");

        return new HashSet<>();
    }

}
