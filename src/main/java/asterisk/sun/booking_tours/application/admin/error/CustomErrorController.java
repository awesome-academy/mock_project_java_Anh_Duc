package asterisk.sun.booking_tours.application.admin.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Custom Error Controller to handle errors like 404, 500, etc.
 * This controller intercepts error pages and provides custom error handling for admin/web pages only.
 * API errors are handled by RestGlobalExceptionHandler.
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    /**
     * Handle all web errors and route to appropriate error page.
     * Only handles non-API requests (admin pages).
     *
     * @param request The HTTP request
     * @param model The model to add attributes to
     * @return The error page view name, or null for API requests
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String path = requestUri != null ? requestUri.toString() : "unknown";

        // Skip handling for API requests - let RestGlobalExceptionHandler handle them
        if (path.startsWith("/api/")) {
            logger.debug("Skipping error handling for API path: {}", path);
            return null;
        }

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            logger.error("Admin/Web Error {} occurred for path: {}", statusCode, path);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // Handle 404 - Not Found
                model.addAttribute("message", "Page Not Found");
                model.addAttribute("details", "The page you are looking for does not exist.");
                return "pages/404";

            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // Handle 403 - Forbidden
                model.addAttribute("message", "Access Denied");
                model.addAttribute("details", "You don't have permission to access this resource.");
                return "pages/403";

            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // Handle 500 - Internal Server Error
                model.addAttribute("message", "Internal Server Error");
                model.addAttribute("details", "An unexpected error occurred. Please try again later.");
                if (exception != null) {
                    logger.error("Exception details: ", (Throwable) exception);
                }
                return "pages/500";
            }
        }

        // Default error page for other status codes
        logger.error("Unhandled error for path: {}", path);
        model.addAttribute("message", "An Error Occurred");
        model.addAttribute("details", "Something went wrong. Please try again later.");
        return "pages/500";
    }
}
