package asterisk.sun.booking_tours.core.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asterisk.sun.booking_tours.core.user.User;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.createdBy " +
            "WHERE r.reviewableType = :reviewableType " +
            "AND r.reviewableId = :reviewableId " +
            "AND r.status = :status " +
            "ORDER BY r.createdAt DESC")
    List<Review> findByReviewableTypeAndReviewableIdAndStatus(
            @Param("reviewableType") ReviewableType reviewableType,
            @Param("reviewableId") Long reviewableId,
            @Param("status") ReviewStatus status);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.createdBy " +
            "WHERE r.reviewableType = :reviewableType " +
            "AND r.reviewableId = :reviewableId " +
            "ORDER BY r.createdAt DESC")
    List<Review> findByReviewableTypeAndReviewableId(
            @Param("reviewableType") ReviewableType reviewableType,
            @Param("reviewableId") Long reviewableId);

    @Query("SELECT COUNT(r) > 0 FROM Review r " +
            "WHERE r.reviewableType = :reviewableType " +
            "AND r.reviewableId = :reviewableId " +
            "AND r.createdBy = :user")
    boolean existsByReviewableTypeAndReviewableIdAndCreatedBy(
            @Param("reviewableType") ReviewableType reviewableType,
            @Param("reviewableId") Long reviewableId,
            @Param("user") User user);

    @Query("SELECT AVG(r.rating) FROM Review r " +
            "WHERE r.reviewableType = :reviewableType " +
            "AND r.reviewableId = :reviewableId " +
            "AND r.status = :status")
    Double getAverageRating(
            @Param("reviewableType") ReviewableType reviewableType,
            @Param("reviewableId") Long reviewableId,
            @Param("status") ReviewStatus status);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.createdBy " +
            "WHERE r.id = :id AND r.createdBy = :user")
    Optional<Review> findByIdAndCreatedBy(
            @Param("id") Long id,
            @Param("user") User user);
}

