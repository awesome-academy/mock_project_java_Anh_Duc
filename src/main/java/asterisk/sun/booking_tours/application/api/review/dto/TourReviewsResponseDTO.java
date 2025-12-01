package asterisk.sun.booking_tours.application.api.review.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourReviewsResponseDTO {
    private Long tourId;
    private String tourName;
    private Integer totalReviews;
    private Double averageRating;
    private List<ReviewResponseDTO> reviews;
}
