package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false; // Or true, depending on if you want @NotNull to handle nulls. Usually null is not valid for password.
            // But let's assume @NotBlank handles null/empty, so we might return true if null to let NotBlank handle it.
            // However, "StrongPassword" implies validity of the password content itself.
            // Let's stick to "if null, invalid" or delegate to NotBlank.
            // Given RegistrationDTO uses @NotBlank, we can return true here if null to avoid duplicate messages,
            // OR enforce it here too. Let's return false for robustness but @NotBlank is better for "required".
            // Actually, if it's null, it's definitely not a strong password.
        }

        // At least 8 characters
        if (password.length() < 8) {
            return false;
        }

        // No whitespace
        if (password.contains(" ")) {
            return false;
        }

        // Check for complexity
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                // Assuming anything else is a special character.
                // Could be more specific if needed, but this covers standard special chars.
                hasSpecial = true;
            }
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
