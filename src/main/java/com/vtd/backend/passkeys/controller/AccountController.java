package com.vtd.backend.passkeys.controller;

import com.vtd.backend.passkeys.config.ProjectConfig;
import com.vtd.backend.passkeys.models.LoginRequest;
import com.vtd.backend.passkeys.models.RegisterRequest;
import com.vtd.backend.passkeys.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ProjectConfig.ACCOUNT_ENDPOINT)
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String response = accountService.registerUser(registerRequest.getUsername(), registerRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String response = accountService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
