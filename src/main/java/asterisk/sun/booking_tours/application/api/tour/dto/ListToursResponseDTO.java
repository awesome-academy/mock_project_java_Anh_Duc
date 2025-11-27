package asterisk.sun.booking_tours.application.api.tour.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListToursResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String departureLocation;
    private String mainDestination;
    private String itinerary;
    private Integer durationDays;
    private BigDecimal price;
}
