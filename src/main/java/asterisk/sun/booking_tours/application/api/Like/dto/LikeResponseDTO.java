package asterisk.sun.booking_tours.application.api.Like.dto;

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

    private Long likeId;

    private Long userId;

    private String userName;

    private LikeableType likeableType;

    private Long likeableId;

    private Long likesCount;
}
