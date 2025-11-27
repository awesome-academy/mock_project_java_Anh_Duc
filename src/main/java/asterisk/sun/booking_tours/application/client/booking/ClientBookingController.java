package asterisk.sun.booking_tours.application.client.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.client.booking.payload.RequestBookingDTO;
import asterisk.sun.booking_tours.application.client.booking.payload.RequestCancelBookingDTO;
import asterisk.sun.booking_tours.application.client.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.client.common.endpoint.ApiV1;
import asterisk.sun.booking_tours.common.aspect.Loggable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiV1.BOOKING_ENDPOINT)
public class ClientBookingController {
    private final ClientBookingService clientBookingService;
    private final ClientCancelBookingService clientCancelBookingService;

    public ClientBookingController(ClientBookingService clientBookingService,
            ClientCancelBookingService clientCancelBookingService) {
        this.clientBookingService = clientBookingService;
        this.clientCancelBookingService = clientCancelBookingService;
    }

    @Loggable
    @PostMapping("/booking")
    public ResponseEntity<SuccessResponse<String>> booking(@Valid @RequestBody RequestBookingDTO requestBookingDTO) {

        clientBookingService.bookTour(requestBookingDTO);

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
