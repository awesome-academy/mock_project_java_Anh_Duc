package asterisk.sun.booking_tours.application.admin.payment;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.payment.dto.PaymentDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.DashboardService;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TopTourStatisticRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TourStatisticDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.payment.Payment;
import asterisk.sun.booking_tours.core.payment.PaymentRepository;
import asterisk.sun.booking_tours.core.payment.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentAdminService extends BaseServiceController<PaymentRepository> {

    private static final Logger logger = LoggerFactory.getLogger(PaymentAdminService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final DashboardService dashboardService;

    public PaymentAdminService(PaymentRepository paymentRepository,
            SimpMessagingTemplate messagingTemplate,
            DashboardService dashboardService) {
        super(paymentRepository);
        this.messagingTemplate = messagingTemplate;
        this.dashboardService = dashboardService;
    }

    /**
     * Query payments by keyword (search by transaction ID, booking code, user name)
     */
    public List<PaymentDTO> queryPaymentsByKeyword(String keyword) {
        List<Payment> payments;

        if (keyword == null || keyword.trim().isEmpty()) {
            payments = repository.findAll();
        } else {
            // Search by transaction ID
            payments = repository.findAll().stream()
                .filter(p ->
                    (p.getTransactionId() != null && p.getTransactionId().toLowerCase().contains(keyword.toLowerCase())) ||
                    (p.getBooking() != null && p.getBooking().getCode() != null && p.getBooking().getCode().toLowerCase().contains(keyword.toLowerCase())) ||
                    (p.getUser() != null && p.getUser().getUsername() != null && p.getUser().getUsername().toLowerCase().contains(keyword.toLowerCase())) ||
                    (p.getUser() != null && p.getUser().getEmail() != null && p.getUser().getEmail().toLowerCase().contains(keyword.toLowerCase()))
                )
                .toList();
        }

        return payments.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Query payments by status
     */
    public List<PaymentDTO> queryPaymentsByStatus(PaymentStatus status) {
        List<Payment> payments = repository.findByStatus(status);

        return payments.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get payment by ID
     */
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        return convertToDTO(payment);
    }

    /**
     * Update payment status
     */
    @Transactional
    public void updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        payment.setStatus(status);
        repository.save(payment);

        // Update booking status based on payment status
        if (payment.getBooking() != null) {
            if (status == PaymentStatus.COMPLETED) {
                payment.getBooking().setStatus(BookingStatus.CONFIRMED);
                // Push top tours statistics update when booking is CONFIRMED
                pushTopToursUpdate();
            } else if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELLED) {
                payment.getBooking().setStatus(BookingStatus.CANCELLED);
            }
        }
    }

    /**
     * Push top tours statistics update via WebSocket
     */
    private void pushTopToursUpdate() {
        try {
            TopTourStatisticRequestDTO request = new TopTourStatisticRequestDTO();
            request.setLimit(10);
            List<TourStatisticDTO> topTours = dashboardService.getTopPopularTours(request);
            messagingTemplate.convertAndSend("/topic/dashboard/top-tours", topTours);
            logger.info("Pushed top tours update via WebSocket");
        } catch (Exception e) {
            logger.error("Failed to push top tours update: {}", e.getMessage());
        }
    }

    /**
     * Delete payment
     */
    @Transactional
    public void deletePayment(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        repository.delete(payment);
    }

    /**
     * Convert Payment entity to PaymentDTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = MapperHelper.map(payment, PaymentDTO.class);

        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getId());
            dto.setUsername(payment.getUser().getUsername());
            dto.setUserEmail(payment.getUser().getEmail());
        }

        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
            dto.setBookingCode(payment.getBooking().getCode());
            dto.setBookingStatus(payment.getBooking().getStatus());

            if (payment.getBooking().getTourDeparture() != null &&
                payment.getBooking().getTourDeparture().getTour() != null) {
                dto.setTourName(payment.getBooking().getTourDeparture().getTour().getName());
            }
        }

        return dto;
    }
}
