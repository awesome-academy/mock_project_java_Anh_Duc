package asterisk.sun.booking_tours.application.api.user.validator;

import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.application.api.user.dto.UserResendVerificationRequestDTO;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UserResendVerificationValidator
        implements ConstraintValidator<ValidUserResendVerificationValidator, UserResendVerificationRequestDTO> {
    private final UserRepository userRepository;

    public UserResendVerificationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(ValidUserResendVerificationValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserResendVerificationRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean isValid = true;

        // Disable default constraint violation
        context.disableDefaultConstraintViolation();

        // Validate email exists
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!userRepository.existsByEmail(dto.getEmail())) {
                context.buildConstraintViolationWithTemplate("Email does not exist")
                        .addPropertyNode("email")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }

}
