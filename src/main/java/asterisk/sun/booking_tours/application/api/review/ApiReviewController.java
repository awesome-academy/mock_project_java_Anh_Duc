package asterisk.sun.booking_tours.application.api.review;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.application.api.review.dto.CreateReviewForTourRequestDTO;
import asterisk.sun.booking_tours.application.api.review.dto.ReviewBySelfResponseDTO;
import asterisk.sun.booking_tours.application.api.review.dto.TourReviewsResponseDTO;
import asterisk.sun.booking_tours.application.api.review.dto.UpdateReviewRequestDTO;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(ApiV1.REVIEW_ENDPOINT)
public class ApiReviewController {
    private final ApiReviewService apiReviewService;

    public ApiReviewController(ApiReviewService apiReviewService) {
        this.apiReviewService = apiReviewService;
    }

    @PostMapping("create-review-for-tour")
    public ResponseEntity<SuccessResponse<String>> createReviewForTour(
            @Valid @RequestBody CreateReviewForTourRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        apiReviewService.createReviewForTour(dto, userDetails);
        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Review created successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("list-reviews-by-self")
    public ResponseEntity<SuccessResponse<List<ReviewBySelfResponseDTO>>> getReviewBySelf(
            @AuthenticationPrincipal UserDetails userDetail) {
        List<ReviewBySelfResponseDTO> reviews = apiReviewService.getReviewBySelf(userDetail);

        SuccessResponse<List<ReviewBySelfResponseDTO>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Reviews retrieved successfully",
                reviews);

        return ResponseEntity.ok(response);
    }

    @GetMapping("tour/{tourId}")
    public ResponseEntity<SuccessResponse<TourReviewsResponseDTO>> getReviewsByTour(
            @PathVariable Long tourId) {
        TourReviewsResponseDTO reviews = apiReviewService.getReviewsByTour(tourId);

        SuccessResponse<TourReviewsResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Tour reviews retrieved successfully",
                reviews);

        return ResponseEntity.ok(response);
    }

    @PutMapping("update")
    public ResponseEntity<SuccessResponse<String>> updateReview(
            @Valid @RequestBody UpdateReviewRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        apiReviewService.updateReview(dto, userDetails);
        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Review updated successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{reviewId}")
    public ResponseEntity<SuccessResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        apiReviewService.deleteReview(reviewId, userDetails);
        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Review deleted successfully");

        return ResponseEntity.ok(response);
    }
}
