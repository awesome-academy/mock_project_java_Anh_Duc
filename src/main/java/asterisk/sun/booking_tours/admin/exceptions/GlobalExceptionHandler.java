package asterisk.sun.booking_tours.admin.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.persistence.EntityNotFoundException;

/**
 * Global exception handler for all admin controllers.
 * Handles exceptions across the whole application using @ControllerAdvice.
 * Provides centralized exception handling with proper logging and user-friendly error pages.
 */
@ControllerAdvice(basePackages = "asterisk.sun.booking_tours.admin.controllers")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle NotFoundException - when a requested resource is not found.
     * Returns a custom 404 error page.
     */
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(NotFoundException ex) {
        logger.error("Resource not found: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("pages/404");
        modelAndView.addObject("message", "Resource Not Found");
        modelAndView.addObject("details", ex.getMessage());
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

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
     * Handle IllegalArgumentException - invalid arguments or business logic violations.
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
}
