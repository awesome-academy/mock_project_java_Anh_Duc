package asterisk.sun.booking_tours.application.api.like.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLikeRequestDTO {
    @NotNull(message = "Review ID must not be null")
    private Long reviewId;
}
