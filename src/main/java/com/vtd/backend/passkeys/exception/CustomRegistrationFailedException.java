package com.vtd.backend.passkeys.exception;

public class CustomRegistrationFailedException extends RuntimeException {

    public CustomRegistrationFailedException(String message) {
        super(message);
    }

    public CustomRegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
