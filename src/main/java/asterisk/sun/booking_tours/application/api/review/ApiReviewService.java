package asterisk.sun.booking_tours.application.api.review;

import java.util.List;

import javax.swing.text.html.parser.Entity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.review.dto.CreateReviewForTourRequestDTO;
import asterisk.sun.booking_tours.application.api.review.dto.ReviewBySelfResponseDTO;
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
}
