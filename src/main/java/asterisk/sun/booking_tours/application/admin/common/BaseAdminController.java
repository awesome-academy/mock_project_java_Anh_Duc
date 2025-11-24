package asterisk.sun.booking_tours.application.admin.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Base controller for admin operations.
 * Provides common functionality including:
 * - Service and view path registration
 * - Logging utilities
 * - Common success/error message handling
 *
 * Note: Exception handling is managed by GlobalExceptionHandler (@ControllerAdvice)
 *
 * @param <S> The service type used by the controller
 */
public abstract class BaseAdminController<S> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final S service;
    protected final String viewPath;

    /**
     * Constructor to initialize service and view path.
     *
     * @param service The service instance to be used by this controller
     * @param viewPath The base view path for this controller (e.g., "pages/user/")
     */
    protected BaseAdminController(S service, String viewPath) {
        this.service = service;
        this.viewPath = viewPath;
        logger.info("Initialized {} with view path: {}", getClass().getSimpleName(), viewPath);
    }

    /**
     * Get the default redirect path for this controller.
     * Child classes must implement this to specify their default redirect behavior.
     *
     * @return The redirect path (e.g., "redirect:/admin/users")
     */
    protected abstract String getDefaultRedirectPath();

    /**
     * Build a complete view path by appending the view name to the base view path.
     *
     * @param viewName The view file name (e.g., "index", "create", "edit")
     * @return The complete view path
     */
    protected String view(String viewName) {
        return viewPath + viewName;
    }

    /**
     * Handle successful operations with a flash message.
     *
     * @param redirectAttributes Redirect attributes for flash messages
     * @param message Success message to display
     * @return The default redirect path
     */
    protected String handleSuccess(RedirectAttributes redirectAttributes, String message) {
        logger.info("Success: {}", message);
        redirectAttributes.addFlashAttribute("successMessage", message);
        return getDefaultRedirectPath();
    }

    /**
     * Handle error operations with a flash message.
     *
     * @param redirectAttributes Redirect attributes for flash messages
     * @param message Error message to display
     * @return The default redirect path
     */
    protected String handleError(RedirectAttributes redirectAttributes, String message) {
        logger.warn("Error: {}", message);
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return getDefaultRedirectPath();
    }

    protected String handleValidationErrors(String viewName, BindingResult bindingResult) {
        logger.warn("Validation Errors found: {}", bindingResult.getFieldError().getDefaultMessage());

        return viewPath + viewName;
    }
}
