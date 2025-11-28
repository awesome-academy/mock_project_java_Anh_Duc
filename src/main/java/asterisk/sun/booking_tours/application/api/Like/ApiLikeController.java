package asterisk.sun.booking_tours.application.api.Like;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.Like.dto.CreateLikeRequestDTO;
import asterisk.sun.booking_tours.application.api.Like.dto.LikeResponseDTO;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiV1.LIKE_ENDPOINT)
public class ApiLikeController {
    private final ApiLikeService apiLikeService;

    public ApiLikeController(ApiLikeService apiLikeService) {
        this.apiLikeService = apiLikeService;
    }

    @PostMapping("toggle")
    public ResponseEntity<SuccessResponse<LikeResponseDTO>> toggleLike(
            @Valid @RequestBody CreateLikeRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        LikeResponseDTO likeResponse = apiLikeService.toggleLike(dto, userDetails);

        SuccessResponse<LikeResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Like toggled successfully",
                likeResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("count")
    public ResponseEntity<SuccessResponse<Long>> getLikesCount(
            @RequestParam String likeableType,
            @RequestParam Long likeableId) {

        Long likesCount = apiLikeService.getLikesCount(likeableType, likeableId);

        SuccessResponse<Long> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Likes count retrieved successfully",
                likesCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("is-liked")
    public ResponseEntity<SuccessResponse<Boolean>> isLikedByCurrentUser(
            @RequestParam String likeableType,
            @RequestParam Long likeableId,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean isLiked = apiLikeService.isLikedByCurrentUser(likeableType, likeableId, userDetails);

        SuccessResponse<Boolean> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Like status retrieved successfully",
                isLiked);

        return ResponseEntity.ok(response);
    }
}
