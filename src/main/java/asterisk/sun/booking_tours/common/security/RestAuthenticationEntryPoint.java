package asterisk.sun.booking_tours.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Authentication Entry Point for handling unauthorized access
 * Returns JSON response for API endpoints and redirects for web endpoints
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/")) {
            // API endpoints - return JSON error with 401 Unauthorized
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "Authentication required");
            error.put("path", requestUri);
            error.put("timestamp", String.valueOf(System.currentTimeMillis()));

            response.getWriter().write(objectMapper.writeValueAsString(error));
        } else {
            // Web endpoints - redirect to login page
            response.sendRedirect("/auth/login");
        }
    }
}
