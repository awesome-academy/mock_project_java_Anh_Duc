package asterisk.sun.booking_tours.application.api.user.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserResendVerificationValidator.class)
@Documented
public @interface ValidUserResendVerificationValidator {
    String message() default "Invalid user registration data";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
