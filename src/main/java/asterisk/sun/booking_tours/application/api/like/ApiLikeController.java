package asterisk.sun.booking_tours.application.api.like;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.application.api.like.dto.CreateLikeRequestDTO;
import asterisk.sun.booking_tours.application.api.like.dto.ToggleLikeCommentRequestDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiV1.LIKE_ENDPOINT)
public class ApiLikeController {
    private final ApiLikeService apiLikeService;

    public ApiLikeController(ApiLikeService apiLikeService) {
        this.apiLikeService = apiLikeService;
    }

    @PostMapping("/toggle-for-review")
    public ResponseEntity<SuccessResponse<String>> toggleLikeForReview(
            @Valid @RequestBody CreateLikeRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        apiLikeService.toggleLikeForReview(dto, userDetails);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Like toggled successfully",
                null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle-for-comment")
    public ResponseEntity<SuccessResponse<String>> toggleLikeForComment(
            @Valid @RequestBody ToggleLikeCommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        apiLikeService.toggleLikeForComment(dto, userDetails);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Like toggled successfully",
                null);

        return ResponseEntity.ok(response);
    }
}
