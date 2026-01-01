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

    @Async("bookingTaskExecutor")
    public CompletableFuture<Void> processCancellationAsync(Booking booking) {
        String threadName = Thread.currentThread().getName();
        logger.info("┌─────────────────────────────────────────────────────────────┐");
        logger.info("│ [{}] 🔄 START processing booking: {}", threadName, booking.getCode());
        logger.info("└─────────────────────────────────────────────────────────────┘");

        long startTime = System.currentTimeMillis();

        try {
            // Simulate some processing time (200ms) to demonstrate parallel execution
            // In real scenario, this could be database operations, external API calls, etc.
            Thread.sleep(200);

            cancelBookingAndRestoreSlots(booking);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("┌─────────────────────────────────────────────────────────────┐");
            logger.info("│ [{}] ✅ DONE booking: {} in {}ms", threadName, booking.getCode(), duration);
            logger.info("└─────────────────────────────────────────────────────────────┘");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("[{}] Interrupted while processing booking {}", threadName, booking.getCode());
        } catch (Exception e) {
            logger.error("[{}] ❌ FAILED booking {}: {}", threadName, booking.getCode(), e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public void cancelBookingAndRestoreSlots(Booking booking) {
        String threadName = Thread.currentThread().getName();

        // Refresh booking from database with TourDeparture eagerly loaded
        // This is necessary because @Async runs in a separate thread without Hibernate session
        Booking freshBooking = bookingRepository.findByIdWithTourDeparture(booking.getId())
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
