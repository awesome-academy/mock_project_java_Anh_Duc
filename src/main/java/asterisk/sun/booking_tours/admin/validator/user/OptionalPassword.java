package asterisk.sun.booking_tours.admin.validator.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for optional password field.
 * Validates password length only if a password is provided (not null and not empty).
 * This is useful for update forms where password change is optional.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalPasswordValidator.class)
public @interface OptionalPassword {

    String message() default "Password must be at least {min} characters when provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum length of password when provided
     */
    int min() default 6;

    /**
     * Maximum length of password when provided
     */
    int max() default 100;
}
