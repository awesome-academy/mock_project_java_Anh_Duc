package asterisk.sun.booking_tours.application.api.comment;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.comment.dto.CreateCommentForReviewRequestDTO;
import asterisk.sun.booking_tours.core.comment.Comment;
import asterisk.sun.booking_tours.core.comment.CommentRepository;
import asterisk.sun.booking_tours.core.comment.CommentableType;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ApiCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ApiCommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public void createCommentForReview(CreateCommentForReviewRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CommentableType commentableType = CommentableType.REVIEW;

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCommentableType(commentableType);
        comment.setCommentableId(request.getReviewId());
        comment.setUser(user);

        if (request.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().equals(user)) {
            throw new SecurityException("User is not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

}
