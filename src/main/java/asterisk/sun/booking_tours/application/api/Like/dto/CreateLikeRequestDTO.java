package asterisk.sun.booking_tours.application.api.Like.dto;

import asterisk.sun.booking_tours.core.like.LikeableType;
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

    @NotNull(message = "Likeable type must not be null")
    private LikeableType likeableType;

    @NotNull(message = "Likeable ID must not be null")
    private Long likeableId;
}
