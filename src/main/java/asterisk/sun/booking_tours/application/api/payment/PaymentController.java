package asterisk.sun.booking_tours.application.api.payment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.application.api.payment.dto.PaymentResponseDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.RequestPaymentDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.UpdatePaymentStatusDTO;
import asterisk.sun.booking_tours.common.aspect.Loggable;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiV1.PAYMENT_ENDPOINT)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Create a new payment for a booking (Internet Banking)
     * POST /api/v1/payments
     */
    @Loggable
    @PostMapping
    public ResponseEntity<SuccessResponse<String>> createPayment(
            @Valid @RequestBody RequestPaymentDTO requestPaymentDTO) {

        paymentService.createPayment(requestPaymentDTO);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.CREATED.value(),
                "Payment created successfully. Please complete the payment at the provided URL.");

        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by ID
     * GET /api/v1/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<PaymentResponseDTO>> getPaymentById(@PathVariable Long id) {

        PaymentResponseDTO paymentResponse = paymentService.getPaymentById(id);

        SuccessResponse<PaymentResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment retrieved successfully",
                paymentResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by transaction ID
     * GET /api/v1/payments/transaction/{transactionId}
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<SuccessResponse<PaymentResponseDTO>> getPaymentByTransactionId(
            @PathVariable String transactionId) {

        PaymentResponseDTO paymentResponse = paymentService.getPaymentByTransactionId(transactionId);

        SuccessResponse<PaymentResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment retrieved successfully",
                paymentResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for a booking
     * GET /api/v1/payments/booking/{bookingId}
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<SuccessResponse<List<PaymentResponseDTO>>> getPaymentsByBookingId(
            @PathVariable Long bookingId) {

        List<PaymentResponseDTO> payments = paymentService.getPaymentsByBookingId(bookingId);

        SuccessResponse<List<PaymentResponseDTO>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payments retrieved successfully",
                payments);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all payments for a user
     * GET /api/v1/payments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<SuccessResponse<List<PaymentResponseDTO>>> getPaymentsByUserId(
            @PathVariable Long userId) {

        List<PaymentResponseDTO> payments = paymentService.getPaymentsByUserId(userId);

        SuccessResponse<List<PaymentResponseDTO>> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payments retrieved successfully",
                payments);

        return ResponseEntity.ok(response);
    }

    /**
     * Update payment status
     * PUT /api/v1/payments/status
     */
    @Loggable
    @PutMapping("/status")
    public ResponseEntity<SuccessResponse<PaymentResponseDTO>> updatePaymentStatus(
            @Valid @RequestBody UpdatePaymentStatusDTO updatePaymentStatusDTO) {

        PaymentResponseDTO paymentResponse = paymentService.updatePaymentStatus(updatePaymentStatusDTO);

        SuccessResponse<PaymentResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment status updated successfully",
                paymentResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Verify payment (simulate bank verification)
     * POST /api/v1/payments/{id}/verify
     */
    @Loggable
    @PostMapping("/{id}/verify")
    public ResponseEntity<SuccessResponse<PaymentResponseDTO>> verifyPayment(@PathVariable Long id) {

        PaymentResponseDTO paymentResponse = paymentService.verifyPayment(id);

        SuccessResponse<PaymentResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment verified and completed successfully",
                paymentResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     * GET /api/v1/payments/health
     */
    @GetMapping("/health")
    public ResponseEntity<SuccessResponse<String>> healthCheck() {
        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment service is running",
                "OK");

        return ResponseEntity.ok(response);
    }
}
