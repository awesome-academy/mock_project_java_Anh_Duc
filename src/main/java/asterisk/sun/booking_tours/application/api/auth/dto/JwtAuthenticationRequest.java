package asterisk.sun.booking_tours.application.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT Authentication Request")
public class JwtAuthenticationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User email", example = "user2@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "123456")
    private String password;
}
