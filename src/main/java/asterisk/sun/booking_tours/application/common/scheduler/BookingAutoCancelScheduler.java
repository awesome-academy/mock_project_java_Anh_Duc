package asterisk.sun.booking_tours.application.common.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.common.email.BookingNotificationService;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;

/**
 * Scheduled service for automatically cancelling overdue bookings
 * and restoring available slots to tour departures.
 *
 * This service runs as a background job using Spring's @Scheduled annotation
 * and processes bookings asynchronously using @Async for better performance.
 */
@Service
public class BookingAutoCancelScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BookingAutoCancelScheduler.class);

    private static final String AUTO_CANCEL_REASON = "Tự động hủy do quá hạn thanh toán";

    private final BookingRepository bookingRepository;
    private final TourDeparturesRepository tourDeparturesRepository;
    private final BookingNotificationService bookingNotificationService;

    public BookingAutoCancelScheduler(
            BookingRepository bookingRepository,
            TourDeparturesRepository tourDeparturesRepository,
            BookingNotificationService bookingNotificationService) {
        this.bookingRepository = bookingRepository;
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.bookingNotificationService = bookingNotificationService;
    }

    /**
     * Scheduled job that runs every 5 minutes to check for overdue bookings.
     * Uses cron expression: runs at 0 seconds, every 5 minutes.
     *
     * The job finds all PENDING bookings that have passed their payment deadline
     * and processes them asynchronously.
     */
    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void checkAndCancelOverdueBookings() {
        logger.info("Starting scheduled job: Check and cancel overdue bookings at {}", LocalDateTime.now());

        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<Booking> overdueBookings = bookingRepository.findOverdueBookings(
                    BookingStatus.PENDING, currentTime);

            if (overdueBookings.isEmpty()) {
                logger.info("No overdue bookings found");
                return;
            }

            logger.info("Found {} overdue booking(s) to process", overdueBookings.size());

            // Process each booking asynchronously
            for (Booking booking : overdueBookings) {
                processCancellationAsync(booking);
            }

        } catch (Exception e) {
            logger.error("Error in scheduled job for checking overdue bookings: {}", e.getMessage(), e);
        }
    }

    /**
     * Alternative scheduled job that runs every hour (at minute 0).
     * This serves as a backup check for any bookings that might have been missed.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    public void hourlyOverdueBookingCheck() {
        logger.info("Starting hourly backup check for overdue bookings at {}", LocalDateTime.now());
        checkAndCancelOverdueBookings();
    }

    /**
     * Asynchronously processes the cancellation of a single booking.
     * This method runs in a separate thread from the bookingTaskExecutor pool.
     *
     * @param booking The booking to cancel
     * @return CompletableFuture indicating completion
     */
    @Async("bookingTaskExecutor")
    public CompletableFuture<Void> processCancellationAsync(Booking booking) {
        logger.info("Async processing cancellation for booking: {} (ID: {})",
                booking.getCode(), booking.getId());

        try {
            cancelBookingAndRestoreSlots(booking);
            logger.info("Successfully cancelled booking: {} and restored {} slot(s)",
                    booking.getCode(), booking.getTotalParticipants());
        } catch (Exception e) {
            logger.error("Failed to cancel booking {}: {}", booking.getCode(), e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Cancels a booking and restores the available slots to the tour departure.
     * This operation is transactional to ensure data consistency.
     *
     * @param booking The booking to cancel
     */
    @Transactional
    public void cancelBookingAndRestoreSlots(Booking booking) {
        // Refresh booking from database to get latest state
        Booking freshBooking = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Booking not found with ID: " + booking.getId()));

        // Double-check that booking is still PENDING
        if (freshBooking.getStatus() != BookingStatus.PENDING) {
            logger.info("Booking {} is no longer PENDING (current status: {}), skipping cancellation",
                    freshBooking.getCode(), freshBooking.getStatus());
            return;
        }

        // Double-check that payment deadline has passed
        if (freshBooking.getPaymentDeadline() == null ||
                !freshBooking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            logger.info("Booking {} payment deadline has not passed yet, skipping cancellation",
                    freshBooking.getCode());
            return;
        }

        // Update booking status to CANCELLED
        freshBooking.setStatus(BookingStatus.CANCELLED);
        freshBooking.setCancellationReason(AUTO_CANCEL_REASON);
        bookingRepository.save(freshBooking);

        // Restore available slots to tour departure
        restoreSlots(freshBooking);

        // Send notification email asynchronously
        sendCancellationNotification(freshBooking);

        logger.info("Booking {} auto-cancelled due to payment deadline exceeded. " +
                "Deadline was: {}, Current time: {}",
                freshBooking.getCode(),
                freshBooking.getPaymentDeadline(),
                LocalDateTime.now());
    }

    /**
     * Restores the available slots to the tour departure after booking cancellation.
     *
     * @param booking The cancelled booking
     */
    private void restoreSlots(Booking booking) {
        TourDeparture tourDeparture = booking.getTourDeparture();

        if (tourDeparture == null) {
            logger.warn("No tour departure associated with booking {}, skipping slot restoration",
                    booking.getCode());
            return;
        }

        int slotsToRestore = booking.getTotalParticipants();

        try {
            tourDeparture.incrementAvailableSlots(slotsToRestore);
            tourDeparturesRepository.save(tourDeparture);

            logger.info("Restored {} slot(s) to tour departure ID: {}. " +
                    "Available slots now: {}/{}",
                    slotsToRestore,
                    tourDeparture.getId(),
                    tourDeparture.getAvailableSlots(),
                    tourDeparture.getTotalSlots());
        } catch (IllegalArgumentException e) {
            logger.error("Failed to restore slots for booking {}: {}",
                    booking.getCode(), e.getMessage());
            // Still allow cancellation to proceed, but log the error
        }
    }

    /**
     * Manual trigger method to cancel a specific overdue booking.
     * Can be called from admin interface or API.
     *
     * @param bookingId The ID of the booking to cancel
     * @return true if cancellation was successful, false otherwise
     */
    @Transactional
    public boolean manualCancelOverdueBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            logger.warn("Booking not found with ID: {}", bookingId);
            return false;
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            logger.warn("Booking {} is not PENDING, cannot auto-cancel", booking.getCode());
            return false;
        }

        if (!booking.isPaymentOverdue()) {
            logger.warn("Booking {} is not overdue yet", booking.getCode());
            return false;
        }

        cancelBookingAndRestoreSlots(booking);
        return true;
    }

    /**
     * Get count of overdue bookings waiting to be cancelled.
     * Useful for monitoring and reporting.
     *
     * @return Number of overdue pending bookings
     */
    public long getOverdueBookingsCount() {
        return bookingRepository.findOverdueBookings(BookingStatus.PENDING, LocalDateTime.now()).size();
    }

    /**
     * Send cancellation notification email to the booking contact.
     * This method is called after booking is cancelled.
     *
     * @param booking The cancelled booking
     */
    private void sendCancellationNotification(Booking booking) {
        try {
            bookingNotificationService.sendBookingAutoCancelledEmail(booking);
        } catch (Exception e) {
            logger.error("Failed to send cancellation notification for booking {}: {}",
                    booking.getCode(), e.getMessage());
            // Don't throw - email failure should not affect cancellation
        }
    }
}
