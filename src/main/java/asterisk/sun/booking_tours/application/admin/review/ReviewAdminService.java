package asterisk.sun.booking_tours.application.admin.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.review.dto.FormUpdateReviewDTO;
import asterisk.sun.booking_tours.application.admin.review.dto.ListReviewDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.review.Review;
import asterisk.sun.booking_tours.core.review.ReviewRepository;
import asterisk.sun.booking_tours.core.review.ReviewStatus;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewAdminService extends BaseServiceController<ReviewRepository> {

    public ReviewAdminService(ReviewRepository reviewRepository) {
        super(reviewRepository);
    }

    public List<ListReviewDTO> queryReviewsByKeyword(String keyword) {
        List<Review> reviews = repository.searchByKeyword(keyword);
        return reviews.stream().map(this::convertToListDTO).collect(Collectors.toList());
    }

    public FormUpdateReviewDTO getReviewById(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        FormUpdateReviewDTO dto = MapperHelper.map(review, FormUpdateReviewDTO.class);
        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getUsername());
            dto.setUserEmail(review.getUser().getEmail());
        }
        return dto;
    }

    public void updateReviewStatus(FormUpdateReviewDTO formUpdateReviewDTO) {
        Review review = repository.findById(formUpdateReviewDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + formUpdateReviewDTO.getId()));

        review.setStatus(formUpdateReviewDTO.getStatus());
        repository.save(review);
    }

    public void deleteReview(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        repository.delete(review);
    }

    public void approveReview(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.APPROVED);
        repository.save(review);
    }

    public void rejectReview(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.REJECTED);
        repository.save(review);
    }

    public void hideReview(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        review.setStatus(ReviewStatus.HIDDEN);
        repository.save(review);
    }

    private ListReviewDTO convertToListDTO(Review review) {
        ListReviewDTO dto = MapperHelper.map(review, ListReviewDTO.class);
        if (review.getUser() != null) {
            dto.setUserName(review.getUser().getUsername());
            dto.setUserEmail(review.getUser().getEmail());
        }
        return dto;
    }
}
