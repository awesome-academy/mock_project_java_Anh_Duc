package asterisk.sun.booking_tours.core.like;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asterisk.sun.booking_tours.core.user.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndLikeableTypeAndLikeableId(User user, LikeableType likeableType, Long likeableId);

    Long countByLikeableTypeAndLikeableId(LikeableType likeableType, Long likeableId);

    List<Like> findByLikeableTypeAndLikeableId(LikeableType likeableType, Long likeableId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM Like l WHERE l.user.id = :userId AND l.likeableType = :likeableType AND l.likeableId = :likeableId")
    boolean existsByUserIdAndLikeableTypeAndLikeableId(@Param("userId") Long userId,
            @Param("likeableType") LikeableType likeableType,
            @Param("likeableId") Long likeableId);

}
