package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator logic for the @StrongPassword annotation.
 * Enforces:
 * - At least 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character
 */
public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    // Regex breakdown:
    // (?=.*[0-9])       At least one digit
    // (?=.*[a-z])       At least one lowercase
    // (?=.*[A-Z])       At least one uppercase
    // (?=.*[@#$%^&+=!]) At least one special char (expand as needed)
    // (?=\S+$)          No whitespace
    // .{8,}             At least 8 characters
    private static final String PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()_\\-])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Null or empty passwords are handled by @NotBlank or @NotNull on the field if needed.
        // However, for security, we usually treat null as invalid if we strictly require a strong password.
        // But following Bean Validation conventions, null is considered valid unless @NotNull is also present.
        // Wait, for a specific format constraint, usually null is valid to allow @NotNull to handle 'required'.
        if (password == null) {
            return true;
        }

        return pattern.matcher(password).matches();
    }
}
