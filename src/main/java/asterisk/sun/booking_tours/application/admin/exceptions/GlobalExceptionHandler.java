package asterisk.sun.booking_tours.application.admin.exceptions;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler for all admin controllers.
 * Handles exceptions across the whole application using @ControllerAdvice.
 * Provides centralized exception handling with proper logging and user-friendly
 * error pages.
 */
@ControllerAdvice(basePackages = "asterisk.sun.booking_tours.application.admin")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle EntityNotFoundException - JPA entity not found.
     * Returns a custom 404 error page.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entity not found: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("pages/404");
        modelAndView.addObject("message", "Entity Not Found");
        modelAndView.addObject("details", "The requested entity does not exist.");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    /**
     * Handle IllegalArgumentException - invalid arguments or business logic
     * violations.
     * Returns a custom error page with the specific error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Invalid Request");
        modelAndView.addObject("details", ex.getMessage());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    /**
     * Handle DataIntegrityViolationException - database constraint violations.
     * Returns a custom error page explaining the constraint violation.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Data Constraint Violation");
        modelAndView.addObject("details",
                "Unable to complete the operation due to data constraints. The record may be in use or violates data rules.");
        modelAndView.setStatus(HttpStatus.CONFLICT);
        return modelAndView;
    }

    /**
     * Handle all other exceptions - catch-all for unexpected errors.
     * Returns a generic error page.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Internal Server Error");
        modelAndView.addObject("details",
                "An unexpected error occurred while processing your request. Please try again later.");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

    /**
     * Handle MethodArgumentNotValidException - validation errors from @Valid annotation.
     * Returns a custom error page with validation error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.error("Validation failed: {}", ex.getMessage());

        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Validation Failed");
        modelAndView.addObject("details", "Please correct the following errors: " + errorDetails);
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    /**
     * Handle BindException - form binding errors.
     * Returns a custom error page with binding error details.
     */
    @ExceptionHandler(BindException.class)
    public ModelAndView handleBindException(BindException ex) {
        logger.error("Form binding failed: {}", ex.getMessage());

        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Form Binding Failed");
        modelAndView.addObject("details", "Please correct the following errors: " + errorDetails);
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    /**
     * Handle ConstraintViolationException - Bean Validation constraint violations.
     * Returns a custom error page with constraint violation details.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ModelAndView handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("Constraint violation: {}", ex.getMessage());

        String errorDetails = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Validation Constraint Violation");
        modelAndView.addObject("details", "Please correct the following errors: " + errorDetails);
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    /**
     * Handle AccessDeniedException - access denied/forbidden errors.
     * Returns a custom 403 error page.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("pages/403");
        modelAndView.addObject("message", "Access Denied");
        modelAndView.addObject("details",
                "You do not have permission to access this resource. Please contact your administrator.");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);
        return modelAndView;
    }

    /**
     * Handle NoHandlerFoundException - 404 not found errors.
     * Returns a custom 404 error page.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex) {
        logger.error("No handler found: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("pages/404");
        modelAndView.addObject("message", "Page Not Found");
        modelAndView.addObject("details", "The page you are looking for does not exist.");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    /**
     * Handle HttpRequestMethodNotSupportedException - unsupported HTTP method.
     * Returns a custom error page.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.error("HTTP method not supported: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Method Not Allowed");
        modelAndView.addObject("details",
                "The HTTP method " + ex.getMethod() + " is not supported for this request. Supported methods: "
                        + String.join(", ", ex.getSupportedMethods()));
        modelAndView.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        return modelAndView;
    }

    /**
     * Handle MissingServletRequestParameterException - missing required request parameter.
     * Returns a custom error page.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        logger.error("Missing request parameter: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("pages/500");
        modelAndView.addObject("message", "Missing Required Parameter");
        modelAndView.addObject("details",
                "Required parameter '" + ex.getParameterName() + "' of type " + ex.getParameterType() + " is missing.");
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }
}
