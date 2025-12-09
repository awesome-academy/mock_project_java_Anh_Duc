package asterisk.sun.booking_tours.application.api.tour;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.tour.dto.ListToursResponseDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.SearchToursRequestDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewDetailResponseDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewTourDeparturesResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;

@Service
public class ApiTourService {
    private final TourRepository tourRepository;

    public ApiTourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public Page<Tour> getListTours(SearchToursRequestDTO request) {
        Pageable pageable = request.getPageable();

        Specification<Tour> spec = (root, query, cb) -> {
            if (request.getKeyword() != null) {
                String likeKey = "%" + request.getKeyword().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), likeKey),
                        cb.like(cb.lower(root.get("description")), likeKey));
            }

            if (request.getMainDestination() != null) {
                String likeLocation = "%" + request.getMainDestination().toLowerCase() + "%";
                return cb.like(cb.lower(root.get("mainDestination")), likeLocation);
            }

            if (request.getDate() != null) {
                return cb.equal(root.join("departures").get("departureDate"), request.getDate());
            }

            return cb.conjunction();
        };

        return tourRepository.findAll(spec, pageable);
    }

    public ViewDetailResponseDTO getTourDetail(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour not found with id: " + tourId));

        ViewDetailResponseDTO responseDTO = MapperHelper.map(tour, ViewDetailResponseDTO.class);

        responseDTO.setDepartures(mapDepartures(tour));

        return responseDTO;
    }

    private List<ViewTourDeparturesResponseDTO> mapDepartures(Tour tour) {
        return MapperHelper.mapList(tour.getDepartures(), ViewTourDeparturesResponseDTO.class);
    }
}
