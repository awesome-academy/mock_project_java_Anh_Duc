package asterisk.sun.booking_tours.core.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.user u " +
            "WHERE (:keyword IS NULL OR :keyword = '') OR " +
            "(LOWER(r.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Review> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.user " +
            "WHERE r.status = :status")
    List<Review> findByStatus(@Param("status") ReviewStatus status);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.user " +
            "WHERE r.reviewableType = :reviewableType")
    List<Review> findByReviewableType(@Param("reviewableType") ReviewableType reviewableType);
}
