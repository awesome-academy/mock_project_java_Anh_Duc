package asterisk.sun.booking_tours.core.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asterisk.sun.booking_tours.core.user.User;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.createdBy u " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Review> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.createdBy " +
            "WHERE r.status = :status")
    List<Review> findByStatus(@Param("status") ReviewStatus status);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.createdBy " +
            "WHERE r.reviewableType = :reviewableType")
    List<Review> findByReviewableType(@Param("reviewableType") ReviewableType reviewableType);

    List<Review> findByCreatedBy(User user);
}
