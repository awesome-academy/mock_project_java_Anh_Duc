package asterisk.sun.booking_tours.application.rest.admin.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import asterisk.sun.booking_tours.core.booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListBookingLatestDTO {
    private String id;
    private String code;
    private String tourName;
    private String contactName;
    private BookingStatus status;
    private BigDecimal finalTotal;
    private LocalDateTime createdAt;
}
