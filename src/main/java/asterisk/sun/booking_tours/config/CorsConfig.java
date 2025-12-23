package asterisk.sun.booking_tours.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép các origin cụ thể (thay thế bằng URL frontend của bạn)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React default port
            "http://localhost:4200",      // Angular default port
            "http://localhost:8080",      // Vue default port
            "http://localhost:5173"       // Vite default port
        ));

        // Cho phép các HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Cho phép tất cả headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // QUAN TRỌNG: Cho phép gửi credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cho phép expose headers để frontend có thể đọc
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Set-Cookie",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));

        // Thời gian cache preflight request (giây)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
