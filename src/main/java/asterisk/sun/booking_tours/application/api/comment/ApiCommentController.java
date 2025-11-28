package asterisk.sun.booking_tours.application.api.comment;

import org.springframework.web.bind.annotation.RequestMapping;

import asterisk.sun.booking_tours.application.api.comment.dto.CreateCommentRequestDTO;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(ApiV1.COMMENT_ENDPOINT)
public class ApiCommentController {
    private final ApiCommentService apiCommentService;

    public ApiCommentController(ApiCommentService apiCommentService) {
        this.apiCommentService = apiCommentService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<String>> comment(@RequestBody CreateCommentRequestDTO request, @AuthenticationPrincipal UserDetails userDetails) {
        apiCommentService.createComment(request, userDetails);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Comment created successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<SuccessResponse<String>> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        apiCommentService.deleteComment(commentId, userDetails);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Comment deleted successfully");

        return ResponseEntity.ok(response);
    }
}
