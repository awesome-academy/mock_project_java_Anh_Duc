package asterisk.sun.booking_tours.application.admin.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for OptionalPassword annotation.
 * Only validates password length when a password is actually provided.
 */
public class OptionalPasswordValidator implements ConstraintValidator<OptionalPassword, String> {

    private int min;
    private int max;

    @Override
    public void initialize(OptionalPassword annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // If password is null or empty, it's valid (optional field)
        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        // If password is provided, validate its length
        int length = password.length();

        if (length < min) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password must be at least " + min + " characters when provided"
            ).addConstraintViolation();
            return false;
        }

        if (length > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password must not exceed " + max + " characters"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
