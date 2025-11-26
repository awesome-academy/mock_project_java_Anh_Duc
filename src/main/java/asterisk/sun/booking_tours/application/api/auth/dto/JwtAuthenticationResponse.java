package asterisk.sun.booking_tours.application.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthenticationResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String email;

    public JwtAuthenticationResponse(String token, String email) {
        this.token = token;
        this.type = "Bearer";
        this.email = email;
    }
}
