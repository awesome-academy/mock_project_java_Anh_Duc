package asterisk.sun.booking_tours.application.api.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.booking.dto.RequestBookingDTO;
import asterisk.sun.booking_tours.application.api.booking.dto.RequestCancelBookingDTO;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.api.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.common.aspect.Loggable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;


@RestController
@RequestMapping(ApiV1.BOOKING_ENDPOINT)
public class ApiBookingController {
    private final ApiBookingService clientBookingService;
    private final CancelBookingService clientCancelBookingService;

    public ApiBookingController(ApiBookingService clientBookingService,
            CancelBookingService clientCancelBookingService) {
        this.clientBookingService = clientBookingService;
        this.clientCancelBookingService = clientCancelBookingService;
    }

    @Loggable
    @PostMapping("/booking")
    public ResponseEntity<SuccessResponse<String>> booking(@Valid @RequestBody RequestBookingDTO requestBookingDTO, @AuthenticationPrincipal UserDetails userDetails) {

        clientBookingService.bookTour(requestBookingDTO, userDetails.getUsername());

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Booking created successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("cancel")
    public ResponseEntity<SuccessResponse<String>> cancelBooking(@Valid @RequestBody RequestCancelBookingDTO requestCancelDTO) {

        clientCancelBookingService.cancel(requestCancelDTO);

        SuccessResponse<String> response = new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Booking cancelled successfully");

        return ResponseEntity.ok(response);
    }

}
