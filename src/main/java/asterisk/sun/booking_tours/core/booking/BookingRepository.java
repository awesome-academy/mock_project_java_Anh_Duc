package asterisk.sun.booking_tours.core.booking;

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
}
