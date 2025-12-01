package asterisk.sun.booking_tours.application.api.user.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom annotation for validating user registration form
 * Validates password matching and uniqueness of username, email, and phone
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserRegistrationValidator.class)
@Documented
public @interface ValidUserRegistration {

    String message() default "Invalid user registration data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
