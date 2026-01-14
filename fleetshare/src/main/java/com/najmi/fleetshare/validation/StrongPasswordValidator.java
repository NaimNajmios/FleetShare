package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Return true for null/blank values to allow @NotNull/@NotBlank to handle mandatory checks.
        if (password == null || password.isBlank()) {
            return true;
        }

        // Check length
        if (password.length() < 12) {
            return false;
        }

        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // Check for digit
        if (!password.matches(".*[0-9].*")) {
            return false;
        }

        // Check for special character (non-alphanumeric)
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            return false;
        }

        return true;
    }
}
