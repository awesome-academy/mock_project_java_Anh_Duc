package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class BaseAdminController {

    protected String handleSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("successMessage", message);
        return getDefaultRedirectPath();
    }

    protected abstract String getDefaultRedirectPath();

    protected abstract void addCommonAttributes(ModelAndView mav);
}
