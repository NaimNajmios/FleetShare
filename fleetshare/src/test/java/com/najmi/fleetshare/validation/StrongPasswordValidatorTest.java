package com.najmi.fleetshare.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    public void testValidPassword() {
        // At least 8 chars, 1 upper, 1 lower, 1 digit, 1 special
        assertTrue(validator.isValid("StrongP@ss1", null));
        assertTrue(validator.isValid("Another$tron9", null));
    }

    @Test
    public void testTooShort() {
        assertFalse(validator.isValid("Short1!", null));
    }

    @Test
    public void testNoUppercase() {
        assertFalse(validator.isValid("weakp@ss1", null));
    }

    @Test
    public void testNoLowercase() {
        assertFalse(validator.isValid("WEAKP@SS1", null));
    }

    @Test
    public void testNoDigit() {
        assertFalse(validator.isValid("WeakP@ssword", null));
    }

    @Test
    public void testNoSpecialChar() {
        assertFalse(validator.isValid("WeakPass1", null));
    }

    @Test
    public void testContainsWhitespace() {
        assertFalse(validator.isValid("Strong P@ss1", null));
    }

    @Test
    public void testNull() {
        // Based on implementation, null returns false.
        assertFalse(validator.isValid(null, null));
    }
}
