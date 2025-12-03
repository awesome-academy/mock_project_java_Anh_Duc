package asterisk.sun.booking_tours.application.api.payment;

import java.lang.ProcessHandle.Info;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/create-info-payment")
    public ResponseEntity<SuccessResponse<InforPaymentResponseDTO>> createPaymentInfo(
            @Valid InforPaymentRequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails) {
        InforPaymentResponseDTO infoPayment = paymentService.createPaymentInfo(requestDTO, userDetails);

        SuccessResponse<InforPaymentResponseDTO> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment information created successfully.",
                infoPayment);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment-booking-tour")
    @Loggable
    public ResponseEntity<SuccessResponse<String>> paymentBookingTour(
            @Valid @RequestBody RequestPaymentDTO requestPaymentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        paymentService.paymentBookingTour(requestPaymentDTO, userDetails);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Payment initiated successfully, please check your email for confirmation.");

        return ResponseEntity.ok(response);
    }
}
