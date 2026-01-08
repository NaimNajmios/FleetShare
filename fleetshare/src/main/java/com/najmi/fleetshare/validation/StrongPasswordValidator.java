package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true; // Let @NotNull or @NotBlank handle nulls
        }

        // At least 8 chars, 1 upper, 1 lower, 1 digit, 1 special, no whitespace
        // Regex:
        // ^                 Start of string
        // (?=.*[0-9])       At least one digit
        // (?=.*[a-z])       At least one lowercase
        // (?=.*[A-Z])       At least one uppercase
        // (?=.*[^a-zA-Z0-9]) At least one special char (anything not alphanumeric)
        // (?=\S+$)          No whitespace
        // .{8,}             At least 8 chars
        // $                 End of string

        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$");
    }
}
