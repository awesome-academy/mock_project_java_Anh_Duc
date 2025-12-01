package asterisk.sun.booking_tours.application.api.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.review.dto.CreateReviewForTourRequestDTO;
import asterisk.sun.booking_tours.application.api.review.dto.ReviewBySelfResponseDTO;
import asterisk.sun.booking_tours.application.api.review.dto.ReviewResponseDTO;
import asterisk.sun.booking_tours.application.api.review.dto.TourReviewsResponseDTO;
import asterisk.sun.booking_tours.application.api.review.dto.UpdateReviewRequestDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.review.Review;
import asterisk.sun.booking_tours.core.review.ReviewRepository;
import asterisk.sun.booking_tours.core.review.ReviewStatus;
import asterisk.sun.booking_tours.core.review.ReviewableType;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ApiReviewService {
    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;

    public ApiReviewService(ReviewRepository reviewRepository, TourRepository tourRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    public void createReviewForTour(CreateReviewForTourRequestDTO request, UserDetails userDetails) {
        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found"));
        User userCreatedBy = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if user already reviewed this tour
        boolean alreadyReviewed = reviewRepository.existsByReviewableTypeAndReviewableIdAndCreatedBy(
                ReviewableType.TOUR, tour.getId(), userCreatedBy);

        if (alreadyReviewed) {
            throw new IllegalStateException("You have already reviewed this tour");
        }

        Review review = new Review();
        review.setReviewableType(ReviewableType.TOUR);
        review.setReviewableId(tour.getId());
        review.setCreatedBy(userCreatedBy);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setStatus(ReviewStatus.PENDING);

        reviewRepository.save(review);
    }

    public List<ReviewBySelfResponseDTO> getReviewBySelf(UserDetails userDetail) {
        User user = userRepository.findByEmail(userDetail.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Review> reviews = reviewRepository.findByCreatedBy(user);

        return MapperHelper.mapList(reviews, ReviewBySelfResponseDTO.class);
    }

    public TourReviewsResponseDTO getReviewsByTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found"));

        // Get only approved reviews
        List<Review> reviews = reviewRepository.findByReviewableTypeAndReviewableIdAndStatus(
                ReviewableType.TOUR, tourId, ReviewStatus.APPROVED);

        List<ReviewResponseDTO> reviewDTOs = reviews.stream()
                .map(this::mapToReviewResponseDTO)
                .collect(Collectors.toList());

        Double avgRating = reviewRepository.getAverageRating(
                ReviewableType.TOUR, tourId, ReviewStatus.APPROVED);

        return TourReviewsResponseDTO.builder()
                .tourId(tour.getId())
                .tourName(tour.getName())
                .totalReviews(reviewDTOs.size())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .reviews(reviewDTOs)
                .build();
    }

    public void updateReview(UpdateReviewRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Review review = reviewRepository.findByIdAndCreatedBy(request.getReviewId(), user)
                .orElseThrow(() -> new EntityNotFoundException("Review not found or you don't have permission to edit it"));

        review.setRating(request.getRating());
        review.setContent(request.getContent());
        // Reset status to PENDING when updated
        review.setStatus(ReviewStatus.PENDING);

        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Review review = reviewRepository.findByIdAndCreatedBy(reviewId, user)
                .orElseThrow(() -> new EntityNotFoundException("Review not found or you don't have permission to delete it"));

        reviewRepository.delete(review);
    }

    private ReviewResponseDTO mapToReviewResponseDTO(Review review) {
        User creator = review.getCreatedBy();
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .status(review.getStatus().name())
                .createdAt(review.getCreatedAt())
                .userId(creator != null ? creator.getId() : null)
                .username(creator != null ? creator.getUsername() : null)
                .userFullName(creator != null ? creator.getFirstName() + " " + creator.getLastName() : null)
                .userAvatarUrl(creator != null ? creator.getAvatarUrl() : null)
                .build();
    }
}
