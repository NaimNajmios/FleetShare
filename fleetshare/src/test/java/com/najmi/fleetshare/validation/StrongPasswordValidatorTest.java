package com.najmi.fleetshare.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    void isValid_withNull_shouldReturnTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_withBlank_shouldReturnTrue() {
        assertTrue(validator.isValid("", null));
    }

    @Test
    void isValid_withShortPassword_shouldReturnFalse() {
        assertFalse(validator.isValid("Ab1!short", null));
    }

    @Test
    void isValid_withNoUppercase_shouldReturnFalse() {
        assertFalse(validator.isValid("password1234!", null));
    }

    @Test
    void isValid_withNoLowercase_shouldReturnFalse() {
        assertFalse(validator.isValid("PASSWORD1234!", null));
    }

    @Test
    void isValid_withNoDigit_shouldReturnFalse() {
        assertFalse(validator.isValid("Password!noDigit", null));
    }

    @Test
    void isValid_withNoSpecialChar_shouldReturnFalse() {
        assertFalse(validator.isValid("Password1234NoSpecial", null));
    }

    @Test
    void isValid_withValidPassword_shouldReturnTrue() {
        assertTrue(validator.isValid("Password1234!", null)); // 13 chars
        assertTrue(validator.isValid("S@f3P@ssw0rd", null)); // 12 chars
    }
}
