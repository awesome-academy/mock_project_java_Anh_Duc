package asterisk.sun.booking_tours.config;

import asterisk.sun.booking_tours.common.security.JwtAuthenticationFilter;
import asterisk.sun.booking_tours.common.security.RestAccessDeniedHandler;
import asterisk.sun.booking_tours.common.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import asterisk.sun.booking_tours.application.admin.auth.AuthAdminService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthAdminService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    @SuppressWarnings("deprecation")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf
                        // Disable CSRF for API endpoints and WebSocket
                        .ignoringRequestMatchers("/api/**", "/ws/**"))
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authz -> authz
                        // ==================== PUBLIC ENDPOINTS ====================
                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**",
                                "/scss/**", "/favicon.ico", "/favicon.svg", "/error", "/access-denied")
                        .permitAll()
                        // Swagger UI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // WebSocket endpoints
                        .requestMatchers("/ws/**").permitAll()

                        // ==================== USER CLIENT API (/api/v1/users/**) ====================
                        // User authentication - public (register, login, verify email)
                        .requestMatchers("/api/v1/users/register", "/api/v1/users/login",
                                "/api/v1/users/verify-email", "/api/v1/users/resend-verification",
                                "/api/v1/users/forgot-password", "/api/v1/users/reset-password")
                        .permitAll()
                        // User API endpoints - require USER or ADMIN role
                        .requestMatchers("/api/v1/users/**").hasAnyRole("USER", "ADMIN")

                        // ==================== ADMIN API (/api/v1/admin/**) ====================
                        // Admin authentication - public (login only)
                        .requestMatchers("/api/v1/admin/auth/login").permitAll()
                        // All Admin API endpoints - require ADMIN role
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // ==================== WEB ADMIN PANEL ====================
                        // Admin web login page
                        .requestMatchers("/admin/auth/login", "/admin/auth/register").permitAll()
                        // Employee list - accessible by ADMIN and USER
                        .requestMatchers("/admin/employees").hasAnyRole("ADMIN", "USER")
                        // Admin web panel - require ADMIN role
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        // Use stateless session for API endpoints
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .usernameParameter("email")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400) // 1 day
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
