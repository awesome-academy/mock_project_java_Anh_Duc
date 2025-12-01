package asterisk.sun.booking_tours.application.api.like;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.like.dto.CreateLikeRequestDTO;
import asterisk.sun.booking_tours.application.api.like.dto.LikeResponseDTO;
import asterisk.sun.booking_tours.core.like.Like;
import asterisk.sun.booking_tours.core.like.LikeRepository;
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

    public LikeResponseDTO createLike(CreateLikeRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if like already exists
        boolean likeExists = likeRepository.existsByUserIdAndLikeableTypeAndLikeableId(
                user.getId(), request.getLikeableType(), request.getLikeableId());

        if (likeExists) {
            throw new IllegalArgumentException("You have already liked this item");
        }

        Like like = new Like();
        like.setUser(user);
        like.setLikeableType(request.getLikeableType());
        like.setLikeableId(request.getLikeableId());

        Like savedLike = likeRepository.save(like);

        Long likesCount = likeRepository.countByLikeableTypeAndLikeableId(
                request.getLikeableType(), request.getLikeableId());

        return LikeResponseDTO.builder()
                .likeId(savedLike.getId())
                .userId(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .likeableType(request.getLikeableType())
                .likeableId(request.getLikeableId())
                .likesCount(likesCount)
                .build();
    }

    public void removeLike(CreateLikeRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Like like = likeRepository.findByUserAndLikeableTypeAndLikeableId(
                user, request.getLikeableType(), request.getLikeableId())
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));

        likeRepository.delete(like);
    }

    public Long getLikesCount(String likeableType, Long likeableId) {
        try {
            return likeRepository.countByLikeableTypeAndLikeableId(
                    Enum.valueOf(asterisk.sun.booking_tours.core.like.LikeableType.class, likeableType),
                    likeableId);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Invalid likeable type: " + likeableType);
        }
    }

    public boolean isLikedByCurrentUser(String likeableType, Long likeableId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        try {
            return likeRepository.existsByUserIdAndLikeableTypeAndLikeableId(
                    user.getId(),
                    Enum.valueOf(asterisk.sun.booking_tours.core.like.LikeableType.class, likeableType),
                    likeableId);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Invalid likeable type: " + likeableType);
        }
    }

    public LikeResponseDTO toggleLike(CreateLikeRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean likeExists = likeRepository.existsByUserIdAndLikeableTypeAndLikeableId(
                user.getId(), request.getLikeableType(), request.getLikeableId());

        if (likeExists) {
            removeLike(request, userDetails);
        } else {
            return createLike(request, userDetails);
        }

        Long likesCount = likeRepository.countByLikeableTypeAndLikeableId(
                request.getLikeableType(), request.getLikeableId());

        return LikeResponseDTO.builder()
                .userId(user.getId())
                .userName(userDetails.getUsername())
                .likeableType(request.getLikeableType())
                .likeableId(request.getLikeableId())
                .likesCount(likesCount)
                .build();
    }
}
