package asterisk.sun.booking_tours.application.common.scheduler;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.common.email.BookingNotificationService;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;

/**
 * Service responsible for processing booking cancellations asynchronously.
 *
 * This service is separated from BookingAutoCancelScheduler to ensure that
 * @Async annotation works correctly. Spring AOP proxy does not intercept
 * self-invocation (calling @Async method from the same class), so we need
 * to put async methods in a separate bean.
 *
 * Multi-threading is achieved through:
 * 1. @Async annotation on processCancellationAsync() method
 * 2. ThreadPoolTaskExecutor "bookingTaskExecutor" configured in AsyncConfig
 * 3. Each booking cancellation runs in a separate thread from the pool
 */
@Service
public class BookingCancellationProcessor {

    private static final Logger logger = LoggerFactory.getLogger(BookingCancellationProcessor.class);

    private static final String AUTO_CANCEL_REASON = "Tự động hủy do quá hạn thanh toán";

    private final BookingRepository bookingRepository;
    private final TourDeparturesRepository tourDeparturesRepository;
    private final BookingNotificationService bookingNotificationService;

    public BookingCancellationProcessor(
            BookingRepository bookingRepository,
            TourDeparturesRepository tourDeparturesRepository,
            BookingNotificationService bookingNotificationService) {
        this.bookingRepository = bookingRepository;
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.bookingNotificationService = bookingNotificationService;
    }

    /**
     * Asynchronously processes the cancellation of a single booking.
     *
     * MULTI-THREADING: This method runs in a separate thread from the
     * "bookingTaskExecutor" thread pool. When multiple bookings need to be
     * cancelled, each one is processed in parallel by different threads.
     *
     * Thread Pool Configuration (from AsyncConfig):
     * - Core pool size: 2 threads
     * - Max pool size: 5 threads
     * - Queue capacity: 100 tasks
     * - Thread name prefix: "BookingAsync-"
     *
     * @param booking The booking to cancel
     * @return CompletableFuture indicating completion
     */
    @Async("bookingTaskExecutor")
    public CompletableFuture<Void> processCancellationAsync(Booking booking) {
        String threadName = Thread.currentThread().getName();
        logger.info("[Thread: {}] Starting async cancellation for booking: {} (ID: {})",
                threadName, booking.getCode(), booking.getId());

        long startTime = System.currentTimeMillis();

        try {
            cancelBookingAndRestoreSlots(booking);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("[Thread: {}] Successfully cancelled booking: {} in {}ms. Restored {} slot(s)",
                    threadName, booking.getCode(), duration, booking.getTotalParticipants());
        } catch (Exception e) {
            logger.error("[Thread: {}] Failed to cancel booking {}: {}",
                    threadName, booking.getCode(), e.getMessage(), e);
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
        String threadName = Thread.currentThread().getName();

        // Refresh booking from database to get latest state
        Booking freshBooking = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Booking not found with ID: " + booking.getId()));

        // Double-check that booking is still PENDING (race condition protection)
        if (freshBooking.getStatus() != BookingStatus.PENDING) {
            logger.info("[Thread: {}] Booking {} is no longer PENDING (current status: {}), skipping",
                    threadName, freshBooking.getCode(), freshBooking.getStatus());
            return;
        }

        // Double-check that payment deadline has passed
        if (freshBooking.getPaymentDeadline() == null ||
                !freshBooking.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            logger.info("[Thread: {}] Booking {} payment deadline has not passed yet, skipping",
                    threadName, freshBooking.getCode());
            return;
        }

        // Update booking status to CANCELLED
        freshBooking.setStatus(BookingStatus.CANCELLED);
        freshBooking.setCancellationReason(AUTO_CANCEL_REASON);
        bookingRepository.save(freshBooking);

        // Restore available slots to tour departure
        restoreSlots(freshBooking);

        // Send notification email asynchronously (also runs in separate thread)
        // sendCancellationNotification(freshBooking);

        logger.info("[Thread: {}] Booking {} auto-cancelled. Deadline was: {}, Current time: {}",
                threadName,
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

            logger.info("Restored {} slot(s) to tour departure ID: {}. Available: {}/{}",
                    slotsToRestore,
                    tourDeparture.getId(),
                    tourDeparture.getAvailableSlots(),
                    tourDeparture.getTotalSlots());
        } catch (IllegalArgumentException e) {
            logger.error("Failed to restore slots for booking {}: {}",
                    booking.getCode(), e.getMessage());
        }
    }

    /**
     * Send cancellation notification email to the booking contact.
     * Email is sent asynchronously via BookingNotificationService.
     *
     * @param booking The cancelled booking
     */
    private void sendCancellationNotification(Booking booking) {
        try {
            // This also runs async in a separate thread
            bookingNotificationService.sendBookingAutoCancelledEmail(booking);
        } catch (Exception e) {
            logger.error("Failed to send cancellation notification for booking {}: {}",
                    booking.getCode(), e.getMessage());
        }
    }
}
