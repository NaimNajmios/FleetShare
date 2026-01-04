package com.najmi.fleetshare.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    public void testValidPassword() {
        // 8 chars, 1 Upper, 1 lower, 1 digit, 1 special
        assertTrue(validator.isValid("StrongP@ss1", context));
    }

    @Test
    public void testNullPassword() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    public void testTooShort() {
        assertFalse(validator.isValid("P@ss1", context));
    }

    @Test
    public void testNoUpperCase() {
        assertFalse(validator.isValid("weakp@ss1", context));
    }

    @Test
    public void testNoLowerCase() {
        assertFalse(validator.isValid("WEAKP@SS1", context));
    }

    @Test
    public void testNoDigit() {
        assertFalse(validator.isValid("WeakP@ssword", context));
    }

    @Test
    public void testNoSpecialChar() {
        assertFalse(validator.isValid("WeakPass1", context));
    }
}
