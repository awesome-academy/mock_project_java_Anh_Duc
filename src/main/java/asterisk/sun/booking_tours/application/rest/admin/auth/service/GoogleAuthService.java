package asterisk.sun.booking_tours.application.rest.admin.auth.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import asterisk.sun.booking_tours.application.rest.admin.auth.dto.LoginResponseDTO;
import asterisk.sun.booking_tours.common.security.JwtUtil;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;

@Service
public class GoogleAuthService {

    @Value("${google.oauth.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public GoogleAuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Verify Google ID token and return user info with JWT token
     */
    public GoogleAuthResult authenticateWithGoogle(String idTokenString) throws GeneralSecurityException, IOException {
        // Verify Google ID token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid Google ID token");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();

        // Get user info from Google
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String avatarUrl = (String) payload.get("picture");
        Boolean emailVerified = payload.getEmailVerified();

        if (!Boolean.TRUE.equals(emailVerified)) {
            throw new IllegalArgumentException("Google email is not verified");
        }

        // Find or create user
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Check if user has ADMIN role for admin login
            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("User does not have admin privileges");
            }
        } else {
            // Create new admin user from Google account
            user = new User();
            user.setEmail(email);
            user.setUsername(email); // Use email as username
            user.setFirstName(firstName != null ? firstName : "");
            user.setLastName(lastName != null ? lastName : "");
            user.setAvatarUrl(avatarUrl);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Random password
            user.setPhone("N/A"); // Google doesn't provide phone
            user.setAddress("N/A"); // Google doesn't provide address
            user.setRole(Role.ADMIN); // Set as ADMIN for admin panel
            user.setStatus(UserStatus.ACTIVE);
            user.setIsVerified(true);
            user = userRepository.save(user);
            isNewUser = true;
        }

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + user.getRole().name());
        String jwtToken = jwtUtil.generateToken(userDetails, claims);

        // Create response DTO
        String fullName = user.getFirstName() + " " + user.getLastName();
        LoginResponseDTO loginResponse = new LoginResponseDTO(
                user.getId().toString(),
                user.getEmail(),
                fullName.trim(),
                user.getRole().name().toLowerCase());

        return new GoogleAuthResult(jwtToken, loginResponse, isNewUser);
    }

    /**
     * Result class for Google authentication
     */
    public static class GoogleAuthResult {
        private final String jwtToken;
        private final LoginResponseDTO userInfo;
        private final boolean newUser;

        public GoogleAuthResult(String jwtToken, LoginResponseDTO userInfo, boolean newUser) {
            this.jwtToken = jwtToken;
            this.userInfo = userInfo;
            this.newUser = newUser;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public LoginResponseDTO getUserInfo() {
            return userInfo;
        }

        public boolean isNewUser() {
            return newUser;
        }
    }
}
