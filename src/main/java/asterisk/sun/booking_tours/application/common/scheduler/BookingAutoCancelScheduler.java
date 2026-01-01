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

    @Scheduled(fixedRate = 30000) // Every 30 seconds for testing
    public void checkAndCancelOverdueBookings() {
        String threadName = Thread.currentThread().getName();
        long jobStartTime = System.currentTimeMillis();

        logger.info("╔══════════════════════════════════════════════════════════════════════════════╗");
        logger.info("║ 🚀 SCHEDULER JOB STARTED                                                      ║");
        logger.info("║ Thread: {} | Time: {}                                  ║",
                String.format("%-15s", threadName), LocalDateTime.now());
        logger.info("╚══════════════════════════════════════════════════════════════════════════════╝");

        try {
            LocalDateTime currentTime = LocalDateTime.now();

            // STEP 1: Query all PENDING bookings for debugging
            logger.info("┌─── STEP 1: Querying all PENDING bookings ───────────────────────────────────┐");
            List<Booking> allPendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING);
            logger.info("│ Total PENDING bookings found: {}                                             │", allPendingBookings.size());

            if (!allPendingBookings.isEmpty()) {
                logger.info("├─── PENDING Bookings Details ────────────────────────────────────────────────┤");
                for (Booking b : allPendingBookings) {
                    boolean isOverdue = b.getPaymentDeadline() != null && b.getPaymentDeadline().isBefore(currentTime);
                    logger.info("│ Booking: {} | Deadline: {} | Overdue: {} │",
                        String.format("%-10s", b.getCode()),
                        b.getPaymentDeadline() != null ? b.getPaymentDeadline() : "NULL",
                        isOverdue ? "✅ YES" : "❌ NO ");
                }
            }
            logger.info("└──────────────────────────────────────────────────────────────────────────────┘");

            // STEP 2: Find overdue bookings
            logger.info("┌─── STEP 2: Filtering OVERDUE bookings ──────────────────────────────────────┐");
            logger.info("│ Current Time: {}                                             │", currentTime);
            List<Booking> overdueBookings = bookingRepository.findOverdueBookings(
                    BookingStatus.PENDING, currentTime);

            if (overdueBookings.isEmpty()) {
                logger.info("│ Result: No overdue bookings found                                            │");
                logger.info("└──────────────────────────────────────────────────────────────────────────────┘");
                logJobCompletion(threadName, jobStartTime, 0);
                return;
            }

            logger.info("│ Found {} overdue booking(s) to cancel                                        │", overdueBookings.size());
            logger.info("└──────────────────────────────────────────────────────────────────────────────┘");

            // STEP 3: Process each booking asynchronously
            logger.info("┌─── STEP 3: Submitting bookings to ASYNC thread pool ────────────────────────┐");
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < overdueBookings.size(); i++) {
                Booking booking = overdueBookings.get(i);
                logger.info("│ [{}/{}] Submitting booking {} for async processing...                       │",
                        i + 1, overdueBookings.size(), booking.getCode());

                CompletableFuture<Void> future = cancellationProcessor.processCancellationAsync(booking);
                futures.add(future);

                logger.debug("│      └─ Submitted to thread pool (bookingTaskExecutor)                      │");
            }
            logger.info("└──────────────────────────────────────────────────────────────────────────────┘");

            // STEP 4: Wait for all async tasks to complete
            logger.info("┌─── STEP 4: Waiting for all ASYNC tasks to complete ─────────────────────────┐");
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenRun(() -> {
                        logger.info("│ ✅ All {} bookings have been processed by worker threads                    │", overdueBookings.size());
                        logger.info("└──────────────────────────────────────────────────────────────────────────────┘");
                        logJobCompletion(threadName, jobStartTime, overdueBookings.size());
                    });

        } catch (Exception e) {
            logger.error("╔══════════════════════════════════════════════════════════════════════════════╗");
            logger.error("║ ❌ SCHEDULER JOB ERROR                                                        ║");
            logger.error("║ Thread: {} | Error: {}                              ║", threadName, e.getMessage());
            logger.error("╚══════════════════════════════════════════════════════════════════════════════╝", e);
        }
    }

    /**
     * Helper method to log job completion with timing information
     */
    private void logJobCompletion(String threadName, long startTime, int processedCount) {
        long duration = System.currentTimeMillis() - startTime;
        logger.info("╔══════════════════════════════════════════════════════════════════════════════╗");
        logger.info("║ ✅ SCHEDULER JOB COMPLETED                                                    ║");
        logger.info("║ Thread: {} | Duration: {}ms | Processed: {} booking(s)           ║",
                String.format("%-15s", threadName), duration, processedCount);
        logger.info("╚══════════════════════════════════════════════════════════════════════════════╝");
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
