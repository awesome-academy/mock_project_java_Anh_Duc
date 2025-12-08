package asterisk.sun.booking_tours.application.api.auth;

import asterisk.sun.booking_tours.application.api.auth.dto.JwtAuthenticationRequest;
import asterisk.sun.booking_tours.application.api.auth.dto.JwtAuthenticationResponse;
import asterisk.sun.booking_tours.common.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Login endpoint - Authenticates user and returns JWT token
     *
     * @param request Login credentials (email and password)
     * @return JWT token with user information
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody JwtAuthenticationRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Extract role
            String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

            // Generate JWT token with additional claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);
            String token = jwtUtil.generateToken(userDetails, claims);

            // Build response
            JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                .token(token)
                .type("Bearer")
                .email(userDetails.getUsername())
                .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            error.put("message", "Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
