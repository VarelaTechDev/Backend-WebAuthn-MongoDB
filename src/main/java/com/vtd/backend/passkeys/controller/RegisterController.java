package com.vtd.backend.passkeys.controller;

import com.vtd.backend.passkeys.config.ProjectConfig;
import com.vtd.backend.passkeys.models.RegistrationStartResponse;
import com.vtd.backend.passkeys.models.RegistrationFinishRequest;
import com.vtd.backend.passkeys.models.ErrorResponse;
import com.vtd.backend.passkeys.exception.CustomRegistrationFailedException;
import com.vtd.backend.passkeys.service.RegistrationService;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(ProjectConfig.REGISTER_ENDPOINT)
public class RegisterController {

    private final RegistrationService registrationService;

    @PostMapping("/registration/start")
    public ResponseEntity<?> startRegistration(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Username cannot be null or empty"));
        }

        try {
            RegistrationStartResponse registrationStartResponse = registrationService.startRegistration(username);
            return new ResponseEntity<>(registrationStartResponse, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/registration/finish")
    public ResponseEntity<String> registrationFinish(@RequestBody RegistrationFinishRequest registrationFinishRequest) {
        System.out.println("MADE IT IN THE CONTROLLER");
        try {
            System.out.println("BEFORE");
            registrationService.finishRegistration(registrationFinishRequest);
            return new ResponseEntity<>("Registration successful", HttpStatus.OK);
        } catch (CustomRegistrationFailedException e) {
            return new ResponseEntity<>("Error finishing registration: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
