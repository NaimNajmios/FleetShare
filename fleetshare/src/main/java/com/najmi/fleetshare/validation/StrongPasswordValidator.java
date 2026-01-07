package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    // Regex explanation:
    // ^                 : Start of string
    // (?=.*[0-9])       : At least one digit
    // (?=.*[a-z])       : At least one lowercase letter
    // (?=.*[A-Z])       : At least one uppercase letter
    // (?=.*[^a-zA-Z0-9\s]) : At least one special character (anything not letter, digit, or whitespace)
    // (?=\S+$)          : No whitespace allowed
    // .{8,}             : At least 8 characters
    // $                 : End of string
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s])(?=\\S+$).{8,}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            // Let @NotNull handle null values
            return true;
        }
        return password.matches(PASSWORD_PATTERN);
    }
}
