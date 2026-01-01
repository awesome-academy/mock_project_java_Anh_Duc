package asterisk.sun.booking_tours.application.rest.admin.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.common.scheduler.BookingAutoCancelScheduler;
import asterisk.sun.booking_tours.application.common.scheduler.BookingCancellationProcessor;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;

/**
 * REST Controller to demonstrate and test Multi-threading in booking cancellation.
 *
 * This controller provides endpoints to:
 * 1. Manually trigger the auto-cancel scheduler
 * 2. Demo multi-threading with multiple bookings
 * 3. Monitor overdue bookings count
 */
@RestController
@RequestMapping("/api/v1/admin/scheduler")
public class SchedulerDemoController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerDemoController.class);

    private final BookingAutoCancelScheduler autoCancelScheduler;
    private final BookingCancellationProcessor cancellationProcessor;
    private final BookingRepository bookingRepository;

    public SchedulerDemoController(
            BookingAutoCancelScheduler autoCancelScheduler,
            BookingCancellationProcessor cancellationProcessor,
            BookingRepository bookingRepository) {
        this.autoCancelScheduler = autoCancelScheduler;
        this.cancellationProcessor = cancellationProcessor;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Manually trigger the auto-cancel scheduler.
     *
     * Usage: POST /api/v1/admin/scheduler/trigger
     */
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerAutoCancel() {
        logger.info("=== MANUAL TRIGGER: Auto-cancel scheduler ===");
        autoCancelScheduler.checkAndCancelOverdueBookings();
        return ResponseEntity.ok("Auto-cancel scheduler triggered. Check logs for details.");
    }

    /**
     * Get count of overdue bookings waiting to be cancelled.
     *
     * Usage: GET /api/v1/admin/scheduler/overdue-count
     */
    @GetMapping("/overdue-count")
    public ResponseEntity<String> getOverdueCount() {
        long count = autoCancelScheduler.getOverdueBookingsCount();
        return ResponseEntity.ok("Overdue bookings count: " + count);
    }

    /**
     * DEMO: Multi-threading demonstration.
     *
     * This endpoint demonstrates how multiple bookings are processed
     * in PARALLEL using different threads from the thread pool.
     *
     * Usage: POST /api/v1/admin/scheduler/demo-multithread?count=5
     *
     * Watch the console logs to see different threads processing bookings simultaneously!
     */
    @PostMapping("/demo-multithread")
    public ResponseEntity<String> demoMultiThreading(
            @RequestParam(defaultValue = "5") int count) {

        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║        MULTI-THREADING DEMONSTRATION START                   ║");
        logger.info("╠══════════════════════════════════════════════════════════════╣");
        logger.info("║ Processing {} bookings using ThreadPool 'bookingTaskExecutor' ║", count);
        logger.info("║ Thread Pool Config:                                          ║");
        logger.info("║   - Core Pool Size: 2 threads                                ║");
        logger.info("║   - Max Pool Size: 5 threads                                 ║");
        logger.info("║   - Thread Name Prefix: 'BookingAsync-'                      ║");
        logger.info("╚══════════════════════════════════════════════════════════════╝");

        // Find overdue PENDING bookings
        LocalDateTime now = LocalDateTime.now();
        List<Booking> overdueBookings = bookingRepository.findOverdueBookings(BookingStatus.PENDING, now);

        if (overdueBookings.isEmpty()) {
            return ResponseEntity.ok(
                "No overdue bookings found. Create some test bookings first with past payment_deadline.");
        }

        // Limit to requested count
        List<Booking> bookingsToProcess = overdueBookings.stream()
                .limit(count)
                .toList();

        logger.info("");
        logger.info("📋 Found {} overdue booking(s) to process:", bookingsToProcess.size());
        for (Booking b : bookingsToProcess) {
            logger.info("   - {} (Deadline: {})", b.getCode(), b.getPaymentDeadline());
        }
        logger.info("");

        // Process each booking ASYNCHRONOUSLY (Multi-threaded)
        String mainThread = Thread.currentThread().getName();
        logger.info("🚀 [Main Thread: {}] Submitting {} tasks to thread pool...", mainThread, bookingsToProcess.size());
        logger.info("");

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (Booking booking : bookingsToProcess) {
            logger.info("📤 [Main Thread: {}] Submitting booking {} to thread pool", mainThread, booking.getCode());

            // This call is @Async - runs in a SEPARATE THREAD from the pool
            CompletableFuture<Void> future = cancellationProcessor.processCancellationAsync(booking);
            futures.add(future);
        }

        logger.info("");
        logger.info("⏳ [Main Thread: {}] All tasks submitted! Waiting for completion...", mainThread);
        logger.info("   (Watch the logs - you'll see different threads processing in parallel!)");
        logger.info("");

        // Wait for all async tasks to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(30, TimeUnit.SECONDS);

            long duration = System.currentTimeMillis() - startTime;

            logger.info("");
            logger.info("╔══════════════════════════════════════════════════════════════╗");
            logger.info("║        MULTI-THREADING DEMONSTRATION COMPLETE                ║");
            logger.info("╠══════════════════════════════════════════════════════════════╣");
            logger.info("║ ✅ Processed {} bookings in {} ms                            ║", bookingsToProcess.size(), duration);
            logger.info("║ 📊 If processed sequentially, would take ~{}ms               ║", bookingsToProcess.size() * 200);
            logger.info("║ ⚡ Speed improvement from parallel processing!               ║");
            logger.info("╚══════════════════════════════════════════════════════════════╝");

            return ResponseEntity.ok(String.format(
                "Multi-threading demo complete!\n" +
                "- Processed: %d bookings\n" +
                "- Total time: %d ms\n" +
                "- Check console logs to see thread names (BookingAsync-1, BookingAsync-2, etc.)",
                bookingsToProcess.size(), duration));

        } catch (Exception e) {
            logger.error("Error waiting for async tasks: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Show current pending bookings with their payment deadlines.
     *
     * Usage: GET /api/v1/admin/scheduler/pending-bookings
     */
    @GetMapping("/pending-bookings")
    public ResponseEntity<List<String>> getPendingBookings() {
        List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING);
        LocalDateTime now = LocalDateTime.now();

        List<String> result = new ArrayList<>();
        result.add("Current time: " + now);
        result.add("---");

        for (Booking b : pendingBookings) {
            String status = b.getPaymentDeadline() != null && b.getPaymentDeadline().isBefore(now)
                    ? "⏰ OVERDUE" : "✅ Not due yet";
            result.add(String.format("%s | Deadline: %s | %s",
                    b.getCode(), b.getPaymentDeadline(), status));
        }

        return ResponseEntity.ok(result);
    }
}
