package asterisk.sun.booking_tours.admin.validator.user;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import asterisk.sun.booking_tours.admin.dto.user.FormUpdateUserDTO;
import asterisk.sun.booking_tours.module.user.User;
import asterisk.sun.booking_tours.module.user.UserService;

@Component
public class FormUpdateUserValidator implements Validator {
    private final UserService userService;

    public FormUpdateUserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormUpdateUserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormUpdateUserDTO formUpdateUserDTO = (FormUpdateUserDTO) target;

        // Validate username uniqueness (exclude current user)
        if (formUpdateUserDTO.getUsername() != null && !formUpdateUserDTO.getUsername().isEmpty()) {
            User existingUser = userService.findByUsername(formUpdateUserDTO.getUsername());
            if (existingUser != null && !existingUser.getId().equals(formUpdateUserDTO.getId())) {
                errors.rejectValue("username", "error.user", "Username already exists");
            }
        }

        // Validate email uniqueness (exclude current user)
        if (formUpdateUserDTO.getEmail() != null && !formUpdateUserDTO.getEmail().isEmpty()) {
            User existingUser = userService.findByEmail(formUpdateUserDTO.getEmail());
            if (existingUser != null && !existingUser.getId().equals(formUpdateUserDTO.getId())) {
                errors.rejectValue("email", "error.user", "Email is already in use");
            }
        }

        // Validate password confirmation (only if password is provided)
        if (formUpdateUserDTO.getPassword() != null && !formUpdateUserDTO.getPassword().isEmpty()) {
            if (formUpdateUserDTO.getConfirmPassword() == null || formUpdateUserDTO.getConfirmPassword().isEmpty()) {
                errors.rejectValue("confirmPassword", "password.required",
                    "Confirm password is required when changing password");
            } else if (!formUpdateUserDTO.getPassword().equals(formUpdateUserDTO.getConfirmPassword())) {
                errors.rejectValue("confirmPassword", "password.mismatch",
                    "Password and confirm password do not match");
            }
        }
    }
}
