package com.najmi.fleetshare.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Password1!",
        "Strong@Pass1",
        "A1@bcdef",
        "MyP@ssw0rd",
        "Password123!",  // Valid: Upper, Lower, Digit, Special
        "Complex_Pass1", // Underscore as special char
        "Bracket[1]Pass",// Brackets as special char
        "Dot.Pass123"    // Dot as special char
    })
    void isValid_ShouldReturnTrue_ForValidPasswords(String password) {
        assertTrue(validator.isValid(password, null), "Expected true for: " + password);
    }

    @Test
    void isValid_ShouldReturnTrue_ForNull() {
        // @NotBlank handles nulls usually, custom validator returns true for null
        assertTrue(validator.isValid(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "password",      // No uppercase, no digit, no special
        "Password",      // No digit, no special
        "Password123",   // No special
        "Pass!23",       // Too short
        "Password! ",    // Contains whitespace
        "ABCDEFGHIJ",    // No lowercase, no digit, no special
        "12345678"       // No letters, no special
    })
    void isValid_ShouldReturnFalse_ForInvalidPasswords(String password) {
        assertFalse(validator.isValid(password, null), "Expected false for: " + password);
    }

    @Test
    void isValid_SpecificInvalidCases() {
        assertFalse(validator.isValid("short1!", null), "Too short");
        assertFalse(validator.isValid("nouppercase1!", null), "No uppercase");
        assertFalse(validator.isValid("NOLOWERCASE1!", null), "No lowercase");
        assertFalse(validator.isValid("NoDigit!", null), "No digit");
        assertFalse(validator.isValid("NoSpecial1", null), "No special char");
        assertFalse(validator.isValid("With Space1!", null), "Has space");
    }
}
