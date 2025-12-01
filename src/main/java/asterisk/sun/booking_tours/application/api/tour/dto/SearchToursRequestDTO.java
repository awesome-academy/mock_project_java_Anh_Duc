package asterisk.sun.booking_tours.application.api.tour.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchToursRequestDTO {
    private String keyword;
    private String mainDestination;
    private LocalDate date;
}
