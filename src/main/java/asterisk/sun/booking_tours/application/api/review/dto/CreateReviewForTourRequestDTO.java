package asterisk.sun.booking_tours.application.api.review.dto;

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
public class CreateReviewForTourRequestDTO {
    @NotNull
    private Long tourId;

    @NotNull
    private Integer rating;

    @NotNull
    @Size(max = 1000)
    private String content;
}
