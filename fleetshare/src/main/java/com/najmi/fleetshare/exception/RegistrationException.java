package com.najmi.fleetshare.exception;

/**
 * Exception thrown during user registration process.
 * Examples: email already exists, password mismatch, validation failures.
 */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
