package asterisk.sun.booking_tours.application.api.like;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.like.dto.CreateLikeRequestDTO;
import asterisk.sun.booking_tours.application.api.like.dto.ToggleLikeCommentRequestDTO;
import asterisk.sun.booking_tours.core.like.Like;
import asterisk.sun.booking_tours.core.like.LikeRepository;
import asterisk.sun.booking_tours.core.like.LikeableType;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ApiLikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public ApiLikeService(LikeRepository likeRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    public void createLike(LikeableType likeableType, Long likeableId, User user) {
        Like like = new Like();
        like.setUser(user);
        like.setLikeableType(likeableType);
        like.setLikeableId(likeableId);

        likeRepository.save(like);
    }

    public void removeLike(LikeableType likeableType, Long likeableId, User user) {
        Like like = likeRepository.findByUserAndLikeableTypeAndLikeableId(
                user, likeableType, likeableId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));

        likeRepository.delete(like);
    }

    public void toggleLikeForReview(CreateLikeRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LikeableType likeableTypeReview = LikeableType.REVIEW;

        boolean likeExists = likeRepository.existsByUserIdAndLikeableTypeAndLikeableId(
                user.getId(), likeableTypeReview, request.getReviewId());

        if (likeExists) {
            removeLike(likeableTypeReview, request.getReviewId(), user);
        } else {
            createLike(likeableTypeReview, request.getReviewId(), user);
        }
    }

    public void toggleLikeForComment(
            ToggleLikeCommentRequestDTO request,
            UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LikeableType likeableTypeComment = LikeableType.COMMENT;

        boolean likeExists = likeRepository.existsByUserIdAndLikeableTypeAndLikeableId(
                user.getId(), likeableTypeComment, request.getCommentId());

        if (likeExists) {
            removeLike(likeableTypeComment, request.getCommentId(), user);
        } else {
            createLike(likeableTypeComment, request.getCommentId(), user);
        }
    }
}
