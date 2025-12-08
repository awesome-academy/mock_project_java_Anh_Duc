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
public class ToggleLikeCommentRequestDTO {
    @NotNull(message = "Comment ID must not be null")
    private Long commentId;
}
