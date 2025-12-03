package asterisk.sun.booking_tours.application.api.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InforPaymentRequestDTO {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
}
