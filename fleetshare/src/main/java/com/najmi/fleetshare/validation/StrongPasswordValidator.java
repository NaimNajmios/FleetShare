package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // Let @NotBlank handle null/empty checks
        }

        // Check length
        if (password.length() < 8) {
            return false;
        }

        // Check for uppercase
        boolean hasUppercase = !password.equals(password.toLowerCase());

        // Check for lowercase
        boolean hasLowercase = !password.equals(password.toUpperCase());

        // Check for digit
        boolean hasDigit = password.matches(".*\\d.*");

        // Check for special character (non-alphanumeric)
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");

        // Check for no whitespace
        boolean hasNoWhitespace = !password.contains(" ");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial && hasNoWhitespace;
    }
}
