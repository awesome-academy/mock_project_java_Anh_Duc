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
     * @param viewName View name (e.g., "create" or "edit")
     * @return View path for the specified form
     */
    public String handleValidationErrors(Model model, String viewName) {
        addFormAttributes(model);
        return ADMIN_USER_VIEW_PATH + viewName;
    }

    /**
     * Handle service layer exceptions
     * @param e Exception thrown
     * @param redirectAttributes RedirectAttributes for flash messages
     * @param model Model to add attributes
     * @param viewName View name (e.g., "create" or "edit")
     * @return View path for the specified form
     */
    public String handleServiceException(Exception e, RedirectAttributes redirectAttributes, Model model, String viewName) {
        // Log the exception for debugging (in production, use proper logging)
        System.err.println("Error processing user: " + e.getMessage());
        e.printStackTrace();

        // Set error message for user
        model.addAttribute("error", "An error occurred while processing the user. Please try again.");
        addFormAttributes(model);
        return ADMIN_USER_VIEW_PATH + viewName;
    }

    /**
     * Handle delete operation exceptions
     * @param e Exception thrown
     * @param redirectAttributes RedirectAttributes for flash messages
     * @param redirectPath Redirect path
     * @return Redirect path
     */
    public String handleDeleteException(Exception e, RedirectAttributes redirectAttributes, String redirectPath) {
        // Log the exception for debugging (in production, use proper logging)
        System.err.println("Error deleting user: " + e.getMessage());
        e.printStackTrace();

        // Set error message for user
        redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        return redirectPath;
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
