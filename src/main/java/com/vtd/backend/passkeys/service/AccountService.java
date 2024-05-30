package com.vtd.backend.passkeys.service;

import com.vtd.backend.passkeys.entities.AccountEntity;
import com.vtd.backend.passkeys.entities.PasswordEntity;
import com.vtd.backend.passkeys.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public String registerUser(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }

        String encryptedPassword = encryptPassword(password);
        PasswordEntity passwordEntity = new PasswordEntity(encryptedPassword);
        AccountEntity accountEntity = new AccountEntity(username, UUID.randomUUID().toString(), passwordEntity, null);
        accountRepository.save(accountEntity);
        return username + " has been successfully registered";
    }

    public String loginUser(String username, String password) {
        Optional<AccountEntity> accountOptional = accountRepository.findByUsername(username);
        if (accountOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        AccountEntity accountEntity = accountOptional.get();
        String encryptedPassword = encryptPassword(password);

        if (!accountEntity.getPassword().getEncryptedPassword().equals(encryptedPassword)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return "Login successful";
    }

    private String encryptPassword(String password) {
        // Simple Base64 encoding for demonstration purposes
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
}
