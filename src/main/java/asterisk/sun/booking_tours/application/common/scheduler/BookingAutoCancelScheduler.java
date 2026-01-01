package asterisk.sun.booking_tours.application.common.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;

/**
 * Scheduled service for automatically cancelling overdue bookings
 * and restoring available slots to tour departures.
 *
 * This service runs as a background job using Spring's @Scheduled annotation.
 *
 * MULTI-THREADING ARCHITECTURE:
 * - Scheduler runs on a single thread (Spring's scheduling thread)
 * - Each booking cancellation is delegated to BookingCancellationProcessor
 * - BookingCancellationProcessor uses @Async to process in parallel threads
 * - Thread pool "bookingTaskExecutor" handles concurrent cancellations
 */
@Service
public class BookingAutoCancelScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BookingAutoCancelScheduler.class);

    private final BookingRepository bookingRepository;
    private final BookingCancellationProcessor cancellationProcessor;

    public BookingAutoCancelScheduler(
            BookingRepository bookingRepository,
            BookingCancellationProcessor cancellationProcessor) {
        this.bookingRepository = bookingRepository;
        this.cancellationProcessor = cancellationProcessor;
    }

    /**
     * Scheduled job that runs every 5 minutes to check for overdue bookings.
     * Uses cron expression: runs at 0 seconds, every 5 minutes.
     *
     * The job finds all PENDING bookings that have passed their payment deadline
     * and processes them asynchronously using MULTI-THREADING.
     *
     * FLOW:
     * 1. Main scheduler thread finds all overdue bookings
     * 2. For each booking, submit to thread pool via @Async
     * 3. Multiple bookings are processed in PARALLEL by worker threads
     * 4. Wait for all tasks to complete (optional)
     */
    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void checkAndCancelOverdueBookings() {
        String threadName = Thread.currentThread().getName();
        logger.info("[Thread: {}] Starting scheduled job: Check and cancel overdue bookings at {}",
                threadName, LocalDateTime.now());

        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<Booking> overdueBookings = bookingRepository.findOverdueBookings(
                    BookingStatus.PENDING, currentTime);

            if (overdueBookings.isEmpty()) {
                logger.info("[Thread: {}] No overdue bookings found", threadName);
                return;
            }

            logger.info("[Thread: {}] Found {} overdue booking(s) to process using multi-threading",
                    threadName, overdueBookings.size());

            // Process each booking asynchronously using MULTI-THREADING
            // Each call to processCancellationAsync runs in a SEPARATE THREAD
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (Booking booking : overdueBookings) {
                // This delegates to BookingCancellationProcessor which has @Async
                // Each booking will be processed by a different thread from the pool
                CompletableFuture<Void> future = cancellationProcessor.processCancellationAsync(booking);
                futures.add(future);
                logger.debug("[Thread: {}] Submitted booking {} for async processing",
                        threadName, booking.getCode());
            }

            // Wait for all async tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenRun(() -> logger.info("[Thread: {}] All {} bookings processed by worker threads",
                            threadName, overdueBookings.size()));

        } catch (Exception e) {
            logger.error("[Thread: {}] Error in scheduled job: {}", threadName, e.getMessage(), e);
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
     * Manual trigger method to cancel a specific overdue booking.
     * Can be called from admin interface or API.
     *
     * @param bookingId The ID of the booking to cancel
     * @return true if cancellation was initiated, false otherwise
     */
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

        // Process asynchronously
        cancellationProcessor.processCancellationAsync(booking);
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
}
