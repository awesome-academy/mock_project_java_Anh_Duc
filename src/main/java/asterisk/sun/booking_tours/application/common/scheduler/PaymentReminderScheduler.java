package asterisk.sun.booking_tours.application.common.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.common.email.BookingNotificationService;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;

/**
 * Scheduled service for sending payment reminder emails to users
 * before their booking payment deadline expires.
 */
@Service
public class PaymentReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReminderScheduler.class);

    private final BookingRepository bookingRepository;
    private final BookingNotificationService bookingNotificationService;

    /**
     * Hours before deadline to send reminder (default: 6 hours)
     */
    @Value("${booking.reminder.hours-before-deadline:6}")
    private int hoursBeforeDeadline;

    public PaymentReminderScheduler(
            BookingRepository bookingRepository,
            BookingNotificationService bookingNotificationService) {
        this.bookingRepository = bookingRepository;
        this.bookingNotificationService = bookingNotificationService;
    }

    /**
     * Scheduled job that runs every hour to check for bookings
     * that are approaching their payment deadline and send reminder emails.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    public void sendPaymentReminders() {
        logger.info("Starting scheduled job: Send payment reminders at {}", LocalDateTime.now());

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderThreshold = now.plusHours(hoursBeforeDeadline);

            // Find pending bookings with deadline approaching
            List<Booking> bookingsNeedingReminder = bookingRepository
                    .findPendingBookingsWithDeadline(BookingStatus.PENDING)
                    .stream()
                    .filter(booking -> {
                        LocalDateTime deadline = booking.getPaymentDeadline();
                        // Send reminder if deadline is within the threshold window
                        // but hasn't passed yet
                        return deadline != null
                                && deadline.isAfter(now)
                                && deadline.isBefore(reminderThreshold);
                    })
                    .toList();

            if (bookingsNeedingReminder.isEmpty()) {
                logger.info("No bookings need payment reminders at this time");
                return;
            }

            logger.info("Found {} booking(s) needing payment reminders", bookingsNeedingReminder.size());

            for (Booking booking : bookingsNeedingReminder) {
                sendReminderAsync(booking);
            }

        } catch (Exception e) {
            logger.error("Error in payment reminder scheduler: {}", e.getMessage(), e);
        }
    }

    /**
     * Send reminder email asynchronously.
     */
    private void sendReminderAsync(Booking booking) {
        try {
            bookingNotificationService.sendPaymentReminderEmail(booking);
            logger.info("Payment reminder sent for booking: {}", booking.getCode());
        } catch (Exception e) {
            logger.error("Failed to send payment reminder for booking {}: {}",
                    booking.getCode(), e.getMessage());
        }
    }
}
