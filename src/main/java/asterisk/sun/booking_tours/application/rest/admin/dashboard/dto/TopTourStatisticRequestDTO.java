package asterisk.sun.booking_tours.application.rest.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopTourStatisticRequestDTO {
    private Integer limit = 10; // Default limit to 10 tours
}
