package asterisk.sun.booking_tours.application.api.like.dto;

import asterisk.sun.booking_tours.core.like.LikeableType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponseDTO {
    private LikeableType likeableType;

    private Long likeableId;

    private Long likesCount;
}
