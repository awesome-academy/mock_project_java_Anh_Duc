package asterisk.sun.booking_tours.application.admin.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.review.dto.FormUpdateReviewDTO;
import asterisk.sun.booking_tours.application.admin.review.dto.ListReviewDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.comment.CommentRepository;
import asterisk.sun.booking_tours.core.comment.CommentableType;
import asterisk.sun.booking_tours.core.like.LikeRepository;
import asterisk.sun.booking_tours.core.like.LikeableType;
import asterisk.sun.booking_tours.core.review.Review;
import asterisk.sun.booking_tours.core.review.ReviewRepository;
import asterisk.sun.booking_tours.core.review.ReviewStatus;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewAdminService extends BaseServiceController<ReviewRepository> {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public ReviewAdminService(ReviewRepository reviewRepository, LikeRepository likeRepository, CommentRepository commentRepository) {
        super(reviewRepository);
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    public List<ListReviewDTO> queryReviewsByKeyword(String keyword) {
        List<Review> reviews = repository.searchByKeyword(keyword);
        return reviews.stream().map(this::convertToListDTO).collect(Collectors.toList());
    }

    public FormUpdateReviewDTO getReviewById(Long id) {
        Review review = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));

        FormUpdateReviewDTO dto = MapperHelper.map(review, FormUpdateReviewDTO.class);
        if (review.getCreatedBy() != null) {
            dto.setUserName(review.getCreatedBy().getUsername());
            dto.setUserEmail(review.getCreatedBy().getEmail());
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
        if (review.getCreatedBy() != null) {
            dto.setUserName(review.getCreatedBy().getUsername());
            dto.setUserEmail(review.getCreatedBy().getEmail());
        }

        // Count likes for this review
        Long likeCount = likeRepository.countByLikeableTypeAndLikeableId(LikeableType.REVIEW, review.getId());
        dto.setLikeCount(likeCount);

        // Count comments for this review
        Long commentCount = commentRepository.countByCommentableTypeAndCommentableId(CommentableType.REVIEW, review.getId());
        dto.setCommentCount(commentCount);

        return dto;
    }
}
