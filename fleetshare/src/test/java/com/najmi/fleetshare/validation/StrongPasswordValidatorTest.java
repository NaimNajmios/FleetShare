package com.najmi.fleetshare.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import com.najmi.fleetshare.dto.RegistrationDTO;
import java.util.Set;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StrongPasswordValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testStrongPassword_Valid() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setFullName("Test User");
        dto.setEmail("test@example.com");
        dto.setAddressLine1("123 Street");
        dto.setCity("City");
        dto.setState("State");
        dto.setPostalCode("12345");
        dto.setUserRole("renter");
        dto.setConfirmPassword("Password123!");
        dto.setAgreeTerms(true);

        dto.setPassword("Password123!"); // Valid

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Should be valid: " + violations);
    }

    @Test
    public void testStrongPassword_ValidWithOtherSpecialChars() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("Password123("); // Using '(' which wasn't in original strict set but is in alphanumeric negation

        // We only check validation for "password" field in this test method style context
        // But validating the whole object might fail other fields.
        // Let's use validator.validateProperty
        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertTrue(violations.isEmpty(), "Should accept parentheses");
    }

    @Test
    public void testStrongPassword_TooShort() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("Pass1!");

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testStrongPassword_NoDigit() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("Password!");

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testStrongPassword_NoUpper() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("password123!");

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testStrongPassword_NoLower() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("PASSWORD123!");

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testStrongPassword_NoSpecial() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("Password123");

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testStrongPassword_Whitespace() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setPassword("Password 123!");

        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validateProperty(dto, "password");
        assertFalse(violations.isEmpty());
    }
}
