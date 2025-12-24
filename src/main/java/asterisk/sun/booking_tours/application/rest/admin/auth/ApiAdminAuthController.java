package asterisk.sun.booking_tours.application.rest.admin.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.auth.dto.JwtAuthenticationRequest;
import asterisk.sun.booking_tours.application.rest.admin.auth.dto.LoginResponseDTO;
import asterisk.sun.booking_tours.application.rest.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.common.security.JwtUtil;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class ApiAdminAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> login(@Valid @RequestBody JwtAuthenticationRequest request,
            HttpServletResponse response) {
        // 1. Thực hiện xác thực
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 2. Lấy thông tin user từ database
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Tạo JWT Token
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        String token = jwtUtil.generateToken(userDetails, claims);

        // 4. Tạo HTTP-ONLY COOKIE
        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();

        // 5. Tạo response theo định dạng mới
        String fullName = user.getFirstName() + " " + user.getLastName();
        LoginResponseDTO userInfo = new LoginResponseDTO(
                user.getId().toString(),
                user.getEmail(),
                fullName,
                user.getRole().name().toLowerCase());

        SuccessResponse<LoginResponseDTO> loginResponse = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Login successful",
                userInfo);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid token or user not authenticated");
        }

        // Lấy thông tin user từ database
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo response
        String fullName = user.getFirstName() + " " + user.getLastName();
        LoginResponseDTO userInfo = new LoginResponseDTO(
                user.getId().toString(),
                user.getEmail(),
                fullName,
                user.getRole().name().toLowerCase());

        SuccessResponse<LoginResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Get current user successful",
                userInfo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout() {
        // Tạo HTTP-ONLY COOKIE với giá trị rỗng và thời gian sống bằng 0
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        SuccessResponse<Void> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Logout successful",
                null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }
}
