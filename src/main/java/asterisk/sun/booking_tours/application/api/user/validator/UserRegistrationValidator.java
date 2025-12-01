package asterisk.sun.booking_tours.application.api.user.validator;

import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.application.api.user.dto.UserRegistrationRequestDTO;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for UserRegistrationRequestDTO
 * Validates:
 * 1. Password and confirm password match
 * 2. Username is unique
 * 3. Email is unique
 * 4. Phone is unique
 */
@Component
public class UserRegistrationValidator implements ConstraintValidator<ValidUserRegistration, UserRegistrationRequestDTO> {

    private final UserRepository userRepository;

    public UserRegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(ValidUserRegistration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRegistrationRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean isValid = true;

        // Disable default constraint violation
        context.disableDefaultConstraintViolation();

        // Validate password match
        if (dto.getPassword() != null && dto.getConfirmPassword() != null) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                context.buildConstraintViolationWithTemplate("Password and confirm password do not match")
                        .addPropertyNode("confirmPassword")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate username uniqueness
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                context.buildConstraintViolationWithTemplate("Username already exists")
                        .addPropertyNode("username")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate email uniqueness
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                context.buildConstraintViolationWithTemplate("Email already exists")
                        .addPropertyNode("email")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate phone uniqueness
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            if (userRepository.existsByPhone(dto.getPhone())) {
                context.buildConstraintViolationWithTemplate("Phone number already exists")
                        .addPropertyNode("phone")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
