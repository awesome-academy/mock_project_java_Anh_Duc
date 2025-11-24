package asterisk.sun.booking_tours.admin.validator.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import asterisk.sun.booking_tours.admin.dto.user.FormCreateUserDTO;
import asterisk.sun.booking_tours.domain.user.UserService;

@Component
public class FormCreateUserValidator implements Validator {
    private final UserService userService;

    public FormCreateUserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormCreateUserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormCreateUserDTO formCreateUserDTO = (FormCreateUserDTO) target;

        // Validate password confirmation
        if (formCreateUserDTO.getPassword() != null && formCreateUserDTO.getConfirmPassword() != null) {
            if (!formCreateUserDTO.getPassword().equals(formCreateUserDTO.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "password.mismatch",
                    "Password and confirm password do not match");
            }
        }

        // Validate username uniqueness
        if (formCreateUserDTO.getUsername() != null && !formCreateUserDTO.getUsername().isEmpty()) {
            if (userService.existsByUsername(formCreateUserDTO.getUsername())) {
                errors.rejectValue("username", "error.user", "Username already exists");
            }
        }

        // Validate email uniqueness
        if (formCreateUserDTO.getEmail() != null && !formCreateUserDTO.getEmail().isEmpty()) {
            if (userService.existsByEmail(formCreateUserDTO.getEmail())) {
                errors.rejectValue("email", "error.user", "Email is already in use");
            }
        }
    }
}
