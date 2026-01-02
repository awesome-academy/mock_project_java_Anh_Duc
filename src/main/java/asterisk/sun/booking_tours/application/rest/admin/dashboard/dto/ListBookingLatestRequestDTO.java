package asterisk.sun.booking_tours.application.rest.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListBookingLatestRequestDTO {
    private int limit;
}
