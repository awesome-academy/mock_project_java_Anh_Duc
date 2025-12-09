package asterisk.sun.booking_tours.application.api.tour.dto;

import java.time.LocalDate;

import asterisk.sun.booking_tours.application.api.common.dto.PaginateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchToursRequestDTO extends PaginateRequest {
    private String keyword;
    private String mainDestination;
    private LocalDate date;
}
