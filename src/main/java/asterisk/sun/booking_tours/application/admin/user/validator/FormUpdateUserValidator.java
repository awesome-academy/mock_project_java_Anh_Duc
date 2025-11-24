
package asterisk.sun.booking_tours.application.admin.user.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import asterisk.sun.booking_tours.application.admin.user.dto.FormUpdateUserDTO;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.application.admin.user.UserAdminService;

@Component
public class FormUpdateUserValidator implements Validator {
    private final UserAdminService userService;

    public FormUpdateUserValidator(UserAdminService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormUpdateUserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormUpdateUserDTO formUpdateUserDTO = (FormUpdateUserDTO) target;
        Long currentUserId = formUpdateUserDTO.getId();

        // Validate username uniqueness (exclude current user)
        if (formUpdateUserDTO.getUsername() != null && !formUpdateUserDTO.getUsername().isEmpty()) {
            if (userService.existsByUsernameExcludingId(formUpdateUserDTO.getUsername(), currentUserId)) {
                errors.rejectValue("username", "error.user", "Username already exists");
            }
        }

        // Validate email uniqueness (exclude current user)
        if (formUpdateUserDTO.getEmail() != null && !formUpdateUserDTO.getEmail().isEmpty()) {
            if (userService.existsByEmailExcludingId(formUpdateUserDTO.getEmail(), currentUserId)) {
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

        // Validate phone uniqueness (exclude current user)
        if (formUpdateUserDTO.getPhone() != null && !formUpdateUserDTO.getPhone().isEmpty()) {
            if (userService.existsByPhoneExcludingId(formUpdateUserDTO.getPhone(), currentUserId)) {
                errors.rejectValue("phone", "error.user", "Phone number is already in use");
            }
        }
    }
}
