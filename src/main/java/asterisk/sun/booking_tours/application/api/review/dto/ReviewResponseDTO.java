package asterisk.sun.booking_tours.application.api.review.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private Long id;
    private Integer rating;
    private String content;
    private String status;
    private LocalDateTime createdAt;

    // User information
    private Long userId;
    private String username;
    private String userFullName;
    private String userAvatarUrl;
}
