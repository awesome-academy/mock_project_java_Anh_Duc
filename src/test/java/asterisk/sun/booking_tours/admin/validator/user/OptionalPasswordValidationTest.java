package asterisk.sun.booking_tours.admin.validator.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import asterisk.sun.booking_tours.admin.dto.user.FormUpdateUserDTO;
import asterisk.sun.booking_tours.domain.user.Role;
import asterisk.sun.booking_tours.domain.user.UserStatus;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OptionalPassword validation.
 * Tests various scenarios of password validation in FormUpdateUserDTO.
 */
@DisplayName("OptionalPassword Validation Tests")
class OptionalPasswordValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private FormUpdateUserDTO createValidDTO() {
        FormUpdateUserDTO dto = new FormUpdateUserDTO();
        dto.setId(1L);
        dto.setUsername("john_doe");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("0123456789");
        dto.setRole(Role.USER);
        dto.setStatus(UserStatus.ACTIVE);
        return dto;
    }

    @Test
    @DisplayName("Should pass validation when password is null (optional)")
    void testPasswordNull() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword(null);

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        // Filter violations to only check password field
        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid when null");
    }

    @Test
    @DisplayName("Should pass validation when password is empty string (optional)")
    void testPasswordEmptyString() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword("");

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid when empty string");
    }

    @Test
    @DisplayName("Should pass validation when password is whitespace only")
    void testPasswordWhitespace() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword("   ");

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid when whitespace only");
    }

    @Test
    @DisplayName("Should pass validation when password meets minimum length")
    void testPasswordValidLength() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword("pass123"); // 7 characters, min is 6

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid when length >= 6");
    }

    @Test
    @DisplayName("Should fail validation when password is too short")
    void testPasswordTooShort() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword("123"); // Only 3 characters, less than min of 6

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertTrue(passwordViolations > 0, "Password should fail validation when too short");

        String message = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .findFirst()
            .map(ConstraintViolation::getMessage)
            .orElse("");

        assertTrue(message.contains("at least 6 characters"),
            "Error message should mention minimum length");
    }

    @Test
    @DisplayName("Should pass validation when password is exactly minimum length")
    void testPasswordExactlyMinLength() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword("123456"); // Exactly 6 characters

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid at exactly min length");
    }

    @Test
    @DisplayName("Should pass validation when password is exactly maximum length")
    void testPasswordExactlyMaxLength() {
        FormUpdateUserDTO dto = createValidDTO();
        // Create a password with exactly 100 characters
        dto.setPassword("a".repeat(100));

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid at exactly max length");
    }

    @Test
    @DisplayName("Should fail validation when password exceeds maximum length")
    void testPasswordTooLong() {
        FormUpdateUserDTO dto = createValidDTO();
        // Create a password with 101 characters (exceeds max of 100)
        dto.setPassword("a".repeat(101));

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertTrue(passwordViolations > 0, "Password should fail validation when too long");

        String message = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .findFirst()
            .map(ConstraintViolation::getMessage)
            .orElse("");

        assertTrue(message.contains("not exceed 100 characters"),
            "Error message should mention maximum length");
    }

    @Test
    @DisplayName("Should validate other fields even when password is empty")
    void testOtherFieldsValidatedWhenPasswordEmpty() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword(""); // Valid (optional)
        dto.setEmail("invalid-email"); // Invalid email format

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        // Should have email validation error, but not password error
        long emailViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("email"))
            .count();

        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertTrue(emailViolations > 0, "Email should fail validation");
        assertEquals(0, passwordViolations, "Password should pass validation when empty");
    }

    @Test
    @DisplayName("Should pass validation with valid password and all other fields")
    void testCompletelyValidDTO() {
        FormUpdateUserDTO dto = createValidDTO();
        dto.setPassword("secure_password_123");
        dto.setConfirmPassword("secure_password_123");

        Set<ConstraintViolation<FormUpdateUserDTO>> violations = validator.validate(dto);

        // Note: This test only validates @OptionalPassword, not confirmPassword matching
        // ConfirmPassword matching is done in FormUpdateUserValidator
        long passwordViolations = violations.stream()
            .filter(v -> v.getPropertyPath().toString().equals("password"))
            .count();

        assertEquals(0, passwordViolations, "Password should be valid");
    }
}
