package asterisk.sun.booking_tours.admin.handlers;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.module.user.Role;
import asterisk.sun.booking_tours.module.user.UserStatus;

@Component
public class UserFormErrorHandler {
    private static final String ADMIN_USER_VIEW_PATH = "pages/user/";

    /**
     * Handle validation errors by adding necessary attributes to model
     * @param model Model to add attributes
     * @return View path for create form
     */
    public String handleValidationErrors(Model model) {
        addFormAttributes(model);
        return ADMIN_USER_VIEW_PATH + "create";
    }

    /**
     * Handle service layer exceptions
     * @param e Exception thrown
     * @param redirectAttributes RedirectAttributes for flash messages
     * @param model Model to add attributes
     * @return View path for create form
     */
    public String handleServiceException(Exception e, RedirectAttributes redirectAttributes, Model model) {
        // Log the exception for debugging (in production, use proper logging)
        System.err.println("Error creating user: " + e.getMessage());
        e.printStackTrace();

        // Set generic error message for user
        redirectAttributes.addFlashAttribute("error", "An error occurred while creating the user. Please try again.");
        addFormAttributes(model);
        return ADMIN_USER_VIEW_PATH + "create";
    }

    /**
     * Add common form attributes needed for the create user form
     * @param model Model to add attributes
     */
    private void addFormAttributes(Model model) {
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());
    }
}
