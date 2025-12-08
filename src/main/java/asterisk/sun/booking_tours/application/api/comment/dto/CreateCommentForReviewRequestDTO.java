package asterisk.sun.booking_tours.application.api.comment.dto;
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
public class CreateCommentForReviewRequestDTO {
    @NotNull(message = "Content must not be null")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;

    @NotNull(message = "Review ID must not be null")
    private Long reviewId;

    private Long parentCommentId;
}
