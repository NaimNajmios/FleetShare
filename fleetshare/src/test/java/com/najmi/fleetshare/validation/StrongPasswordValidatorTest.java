package com.najmi.fleetshare.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    void testValidPassword() {
        assertTrue(validator.isValid("StrongPass1!", null));
    }

    @Test
    void testTooShort() {
        assertFalse(validator.isValid("Pass1!", null));
    }

    @Test
    void testNoUppercase() {
        assertFalse(validator.isValid("password1!", null));
    }

    @Test
    void testNoLowercase() {
        assertFalse(validator.isValid("PASSWORD1!", null));
    }

    @Test
    void testNoDigit() {
        assertFalse(validator.isValid("Password!", null));
    }

    @Test
    void testNoSpecialChar() {
        assertFalse(validator.isValid("Password123", null));
    }

    @Test
    void testWithWhitespace() {
        assertFalse(validator.isValid("Strong Pass1!", null));
    }

    @Test
    void testNull() {
        // Should be true as @NotBlank handles validation of nulls
        assertTrue(validator.isValid(null, null));
    }
}
