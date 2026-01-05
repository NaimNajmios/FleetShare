package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenPasswordIsNull() {
        // Null is considered valid to allow @NotNull to handle requirement
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void isValid_ShouldReturnTrue_WhenPasswordIsStrong() {
        assertTrue(validator.isValid("Password123!", context));
        assertTrue(validator.isValid("Strong@1", context));
        assertTrue(validator.isValid("CompLex#9", context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "weak",              // Too short
        "nouppercase1!",     // No uppercase
        "NOLOWERCASE1!",     // No lowercase
        "NoDigit!!!",        // No digit
        "NoSpecialChar123",  // No special char
        "Short1!",           // Short (7 chars)
        "Has Space 1!",      // Has space
    })
    void isValid_ShouldReturnFalse_WhenPasswordIsWeak(String password) {
        assertFalse(validator.isValid(password, context), "Should be invalid: " + password);
    }
}
