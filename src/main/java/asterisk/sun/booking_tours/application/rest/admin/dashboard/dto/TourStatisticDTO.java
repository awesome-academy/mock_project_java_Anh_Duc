package asterisk.sun.booking_tours.application.rest.admin.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourStatisticDTO {
    private String tourId;
    private String tourName;
    private Long bookingCount;
    private BigDecimal revenue;
}
