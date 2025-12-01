package asterisk.sun.booking_tours.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Access Denied Handler for handling forbidden access
 * Returns JSON response for API endpoints and redirects for web endpoints
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/")) {
            // API endpoints - return JSON error with 403 Forbidden
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            Map<String, String> error = new HashMap<>();
            error.put("error", "Forbidden");
            error.put("message", "Access denied");
            error.put("path", requestUri);
            error.put("timestamp", String.valueOf(System.currentTimeMillis()));

            response.getWriter().write(objectMapper.writeValueAsString(error));
        } else {
            // Web endpoints - redirect to access denied page
            response.sendRedirect("/access-denied");
        }
    }
}
