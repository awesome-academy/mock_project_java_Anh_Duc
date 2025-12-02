package asterisk.sun.booking_tours.core.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asterisk.sun.booking_tours.core.entities.Payment;
import asterisk.sun.booking_tours.core.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payments by booking ID
     */
    List<Payment> findByBookingId(Long bookingId);

    /**
     * Find payments by user ID
     */
    List<Payment> findByUserId(Long userId);

    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find payments by booking ID and status
     */
    @Query("SELECT p FROM Payment p WHERE p.booking.id = :bookingId AND p.status = :status")
    List<Payment> findByBookingIdAndStatus(@Param("bookingId") Long bookingId, @Param("status") PaymentStatus status);

    /**
     * Check if transaction ID exists
     */
    boolean existsByTransactionId(String transactionId);
}
