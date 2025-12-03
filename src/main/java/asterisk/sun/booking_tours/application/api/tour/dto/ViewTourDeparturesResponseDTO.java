package asterisk.sun.booking_tours.application.api.tour.dto;

import java.time.LocalDate;

import asterisk.sun.booking_tours.core.tourdepartures.TourDepartureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewTourDeparturesResponseDTO {
    private Long id;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private int availableSlots;
    private int totalSlots;
    private TourDepartureStatus status;
}
