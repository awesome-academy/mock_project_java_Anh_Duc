package asterisk.sun.booking_tours.application.api.payment;

import java.lang.ProcessHandle.Info;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.application.api.payment.dto.InforPaymentRequestDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.InforPaymentResponseDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.RequestPaymentDTO;
import asterisk.sun.booking_tours.common.aspect.Loggable;
import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiV1.PAYMENT_ENDPOINT)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/info")
    public ResponseEntity<SuccessResponse<InforPaymentResponseDTO>> getInfoPaymentForBooking(
            @Valid InforPaymentRequestDTO requestDTO) {
        InforPaymentRequestDTO request = InforPaymentRequestDTO.builder()
                .bookingId(requestDTO.getBookingId())
                .build();
        InforPaymentResponseDTO infoPayment = paymentService.getInfoPaymentForBooking(request);

        SuccessResponse<InforPaymentResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment information retrieved successfully.",
                infoPayment);

        return ResponseEntity.ok(response);
    }

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
}
