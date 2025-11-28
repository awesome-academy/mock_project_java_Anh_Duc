package asterisk.sun.booking_tours.application.api.review.dto;

import asterisk.sun.booking_tours.core.review.ReviewableType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewBySelfResponseDTO {
    private Long reviewId;

    private ReviewableType reviewableType;

    private Integer rating;

    private String content;

    private String status;
}
