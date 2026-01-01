package asterisk.sun.booking_tours.application.common.email;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import asterisk.sun.booking_tours.core.booking.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending booking-related notification emails asynchronously.
 */
@Service
public class BookingNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(BookingNotificationService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public BookingNotificationService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send email notification when a booking is auto-cancelled due to payment deadline expiration.
     * This method runs asynchronously to avoid blocking the scheduler.
     *
     * @param booking The cancelled booking
     * @return CompletableFuture for async tracking
     */
    @Async("bookingTaskExecutor")
    public CompletableFuture<Boolean> sendBookingAutoCancelledEmail(Booking booking) {
        logger.info("Sending auto-cancel notification email for booking: {}", booking.getCode());

        try {
            String subject = "Thông báo hủy đặt tour - " + booking.getCode();
            String htmlContent = buildAutoCancelEmailContent(booking);

            sendHtmlEmail(booking.getContactEmail(), subject, htmlContent);

            logger.info("Auto-cancel email sent successfully for booking: {}", booking.getCode());
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("Failed to send auto-cancel email for booking {}: {}",
                    booking.getCode(), e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Send payment reminder email before deadline.
     * This can be used to remind users to complete payment.
     *
     * @param booking The booking with approaching deadline
     * @return CompletableFuture for async tracking
     */
    @Async("bookingTaskExecutor")
    public CompletableFuture<Boolean> sendPaymentReminderEmail(Booking booking) {
        logger.info("Sending payment reminder email for booking: {}", booking.getCode());

        try {
            String subject = "Nhắc nhở thanh toán - " + booking.getCode();
            String htmlContent = buildPaymentReminderEmailContent(booking);

            sendHtmlEmail(booking.getContactEmail(), subject, htmlContent);

            logger.info("Payment reminder email sent successfully for booking: {}", booking.getCode());
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("Failed to send payment reminder email for booking {}: {}",
                    booking.getCode(), e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Build HTML content for auto-cancel notification email using Thymeleaf template.
     */
    private String buildAutoCancelEmailContent(Booking booking) {
        Context context = new Context();
        context.setVariable("contactName", booking.getContactName());
        context.setVariable("bookingCode", booking.getCode());
        context.setVariable("tourName", booking.getTourDeparture() != null &&
                booking.getTourDeparture().getTour() != null
                ? booking.getTourDeparture().getTour().getName() : "N/A");
        context.setVariable("departureDate", booking.getTourDeparture() != null
                ? booking.getTourDeparture().getDepartureDate().toString() : "N/A");
        context.setVariable("numAdults", booking.getNumAdults());
        context.setVariable("numChild", booking.getNumChild());
        context.setVariable("finalTotal", booking.getFinalTotal());
        context.setVariable("paymentDeadline", booking.getPaymentDeadline() != null
                ? booking.getPaymentDeadline().format(DATE_FORMATTER) : "N/A");
        context.setVariable("cancellationReason", booking.getCancellationReason());
        context.setVariable("baseUrl", baseUrl);

        try {
            return templateEngine.process("email/booking-auto-cancelled", context);
        } catch (Exception e) {
            logger.warn("Thymeleaf template not found, using fallback HTML content");
            return buildFallbackAutoCancelHtmlContent(booking);
        }
    }

    /**
     * Build HTML content for payment reminder email using Thymeleaf template.
     */
    private String buildPaymentReminderEmailContent(Booking booking) {
        Context context = new Context();
        context.setVariable("contactName", booking.getContactName());
        context.setVariable("bookingCode", booking.getCode());
        context.setVariable("tourName", booking.getTourDeparture() != null &&
                booking.getTourDeparture().getTour() != null
                ? booking.getTourDeparture().getTour().getName() : "N/A");
        context.setVariable("finalTotal", booking.getFinalTotal());
        context.setVariable("paymentDeadline", booking.getPaymentDeadline() != null
                ? booking.getPaymentDeadline().format(DATE_FORMATTER) : "N/A");
        context.setVariable("baseUrl", baseUrl);

        try {
            return templateEngine.process("email/booking-payment-reminder", context);
        } catch (Exception e) {
            logger.warn("Thymeleaf template not found, using fallback HTML content");
            return buildFallbackReminderHtmlContent(booking);
        }
    }

    /**
     * Fallback HTML content if Thymeleaf template is not available.
     */
    private String buildFallbackAutoCancelHtmlContent(Booking booking) {
        String tourName = booking.getTourDeparture() != null && booking.getTourDeparture().getTour() != null
                ? booking.getTourDeparture().getTour().getName() : "N/A";
        String deadline = booking.getPaymentDeadline() != null
                ? booking.getPaymentDeadline().format(DATE_FORMATTER) : "N/A";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Thông báo hủy đặt tour</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #e74c3c;">Thông báo hủy đặt tour</h2>

                    <p>Xin chào <strong>%s</strong>,</p>

                    <p>Chúng tôi rất tiếc phải thông báo rằng đơn đặt tour của bạn đã bị <strong>tự động hủy</strong> do quá hạn thanh toán.</p>

                    <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0;">Thông tin đặt tour:</h3>
                        <ul style="list-style: none; padding: 0;">
                            <li><strong>Mã đặt tour:</strong> %s</li>
                            <li><strong>Tour:</strong> %s</li>
                            <li><strong>Số người lớn:</strong> %d</li>
                            <li><strong>Số trẻ em:</strong> %d</li>
                            <li><strong>Tổng tiền:</strong> %s VNĐ</li>
                            <li><strong>Hạn thanh toán:</strong> %s</li>
                        </ul>
                    </div>

                    <p><strong>Lý do hủy:</strong> %s</p>

                    <p>Nếu bạn vẫn muốn đặt tour, vui lòng truy cập website của chúng tôi để đặt lại.</p>

                    <p>Nếu có bất kỳ thắc mắc nào, xin vui lòng liên hệ với chúng tôi.</p>

                    <p>Trân trọng,<br>Đội ngũ Booking Tours</p>
                </div>
            </body>
            </html>
            """,
            booking.getContactName(),
            booking.getCode(),
            tourName,
            booking.getNumAdults(),
            booking.getNumChild(),
            booking.getFinalTotal() != null ? String.format("%,.0f", booking.getFinalTotal()) : "N/A",
            deadline,
            booking.getCancellationReason()
        );
    }

    /**
     * Fallback HTML content for payment reminder if Thymeleaf template is not available.
     */
    private String buildFallbackReminderHtmlContent(Booking booking) {
        String tourName = booking.getTourDeparture() != null && booking.getTourDeparture().getTour() != null
                ? booking.getTourDeparture().getTour().getName() : "N/A";
        String deadline = booking.getPaymentDeadline() != null
                ? booking.getPaymentDeadline().format(DATE_FORMATTER) : "N/A";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Nhắc nhở thanh toán</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #f39c12;">Nhắc nhở thanh toán</h2>

                    <p>Xin chào <strong>%s</strong>,</p>

                    <p>Đây là email nhắc nhở về việc thanh toán cho đơn đặt tour của bạn.</p>

                    <div style="background-color: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0; border: 1px solid #ffc107;">
                        <p style="margin: 0;"><strong>⚠️ Lưu ý:</strong> Đơn đặt tour của bạn sẽ bị tự động hủy nếu không thanh toán trước <strong>%s</strong></p>
                    </div>

                    <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <h3 style="margin-top: 0;">Thông tin đặt tour:</h3>
                        <ul style="list-style: none; padding: 0;">
                            <li><strong>Mã đặt tour:</strong> %s</li>
                            <li><strong>Tour:</strong> %s</li>
                            <li><strong>Tổng tiền:</strong> %s VNĐ</li>
                        </ul>
                    </div>

                    <p>Vui lòng thanh toán sớm để giữ chỗ của bạn.</p>

                    <p>Trân trọng,<br>Đội ngũ Booking Tours</p>
                </div>
            </body>
            </html>
            """,
            booking.getContactName(),
            deadline,
            booking.getCode(),
            tourName,
            booking.getFinalTotal() != null ? String.format("%,.0f", booking.getFinalTotal()) : "N/A"
        );
    }

    /**
     * Send HTML email using JavaMailSender.
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(fromEmail);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
