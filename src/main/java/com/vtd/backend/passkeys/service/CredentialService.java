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

    /**
     * Look up all credentials with the given credential ID, regardless of what user they're registered to.
     *
     * This is used to refuse registration of duplicate credential IDs. Therefore, under normal
     * circumstances this method should only return zero or one credential (this is an expected
     * consequence, not an interface requirement).
     *
     * @param credentialId the credential ID to look up
     * @return a set of RegisteredCredential with the given credential ID
     */
    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
//        List<AccountEntity> accounts = mongoTemplate.findAll(AccountEntity.class);
//        Set<RegisteredCredential> registeredCredentials = new HashSet<>();
//
//        for (AccountEntity account : accounts) {
//            for (PasskeyEntity passkey : account.getPasskeys()) {
//                if (passkey.getCredentialId().equals(credentialId.getBase64())) {
//                    registeredCredentials.add(RegisteredCredential.builder()
//                            .credentialId(credentialId)
//                            .userHandle(new ByteArray(account.getAccountId().getBytes()))
//                            .publicKeyCose(new ByteArray(passkey.getPublicKey()))
//                            .signatureCount(passkey.getCount())
//                            .build());
//                }
//            }
//        }
//
//        return registeredCredentials;
        return new HashSet<>();
    }
}
