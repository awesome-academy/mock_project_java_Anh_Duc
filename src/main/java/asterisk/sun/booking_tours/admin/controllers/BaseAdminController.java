package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class BaseAdminController {

    protected String handleSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("successMessage", message);
        return getDefaultRedirectPath();
    }

    protected String handleError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return getDefaultRedirectPath();
    }

    protected abstract String getDefaultRedirectPath();
}
