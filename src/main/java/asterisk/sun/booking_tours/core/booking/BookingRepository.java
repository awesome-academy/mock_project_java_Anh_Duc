package asterisk.sun.booking_tours.core.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Search bookings by keyword (code, contact name, contact email, contact phone)
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.user u " +
            "LEFT JOIN FETCH b.tourDeparture td " +
            "LEFT JOIN FETCH td.tour t " +
            "WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(b.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.contactName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.contactEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.contactPhone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Booking> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find bookings by status
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.user u " +
            "LEFT JOIN FETCH b.tourDeparture td " +
            "LEFT JOIN FETCH td.tour t " +
            "WHERE b.status = :status")
    List<Booking> findByStatus(@Param("status") BookingStatus status);

    /**
     * Find bookings by user ID
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.user u " +
            "LEFT JOIN FETCH b.tourDeparture td " +
            "WHERE u.id = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);

    /**
     * Check if booking code exists
     */
    boolean existsByCode(String code);

    /**
     * Find booking by code
     */
    Booking findByCode(String code);

    /**
     * Find overdue pending bookings that need to be auto-cancelled
     * A booking is considered overdue if:
     * - Status is PENDING
     * - Payment deadline has passed
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.tourDeparture td " +
            "WHERE b.status = :status " +
            "AND b.paymentDeadline IS NOT NULL " +
            "AND b.paymentDeadline < :currentTime")
    List<Booking> findOverdueBookings(
            @Param("status") BookingStatus status,
            @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find pending bookings with payment deadline
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.tourDeparture td " +
            "LEFT JOIN FETCH b.user u " +
            "WHERE b.status = :status " +
            "AND b.paymentDeadline IS NOT NULL")
    List<Booking> findPendingBookingsWithDeadline(@Param("status") BookingStatus status);

    /**
     * Find booking by ID with tour departure eagerly loaded.
     * This is useful for async operations where the session may be closed.
     */
    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.tourDeparture td " +
            "WHERE b.id = :id")
    java.util.Optional<Booking> findByIdWithTourDeparture(@Param("id") Long id);

    /**
     * Find top N latest bookings ordered by creation date descending
     */
    List<Booking> findAllByOrderByCreatedAtDesc(org.springframework.data.domain.Pageable pageable);
}
