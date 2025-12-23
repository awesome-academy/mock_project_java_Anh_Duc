package asterisk.sun.booking_tours.application.rest.admin.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.auth.dto.JwtAuthenticationRequest;
import asterisk.sun.booking_tours.common.security.JwtUtil;
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
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody JwtAuthenticationRequest request, HttpServletResponse response) {
        try {
            // 1. Thực hiện xác thực (giữ nguyên logic của bạn)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 2. Tạo JWT Token (giữ nguyên logic của bạn)
            // Extract role
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);
            String token = jwtUtil.generateToken(userDetails, claims);

            // 3. TẠO HTTP-ONLY COOKIE
            ResponseCookie cookie = ResponseCookie.from("access_token", token)
                    .httpOnly(true) // Quan trọng nhất: Chống XSS
                    .secure(false) // Để false nếu bạn đang test ở localhost (http), để true nếu chạy https
                    .path("/") // Cookie có hiệu lực cho toàn bộ domain
                    .maxAge(7 * 24 * 60 * 60) // Thời gian sống (ví dụ 7 ngày)
                    .sameSite("Lax") // Chống CSRF cơ bản
                    .build();

            // 4. TRẢ VỀ RESPONSE
            // Token không còn nằm trong body, chỉ trả về thông tin User cơ bản cho UI
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", userDetails.getUsername());
            userInfo.put("role", userDetails.getAuthorities());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(userInfo);

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
