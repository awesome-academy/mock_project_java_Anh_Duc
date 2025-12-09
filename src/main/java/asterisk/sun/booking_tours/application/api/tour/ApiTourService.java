package asterisk.sun.booking_tours.application.api.tour;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import asterisk.sun.booking_tours.application.api.tour.dto.SearchToursRequestDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewDetailResponseDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewTourDeparturesResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import jakarta.persistence.criteria.Predicate;

@Service
public class ApiTourService {
    private final TourRepository tourRepository;

    public ApiTourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public Page<Tour> getListTours(SearchToursRequestDTO request) {
        Pageable pageable = request.getPageable();
        Specification<Tour> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likeKey = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("name")), likeKey),
                        cb.like(cb.lower(root.get("description")), likeKey));
                predicates.add(keywordPredicate);
            }

            if (request.getMainDestination() != null && !request.getMainDestination().isEmpty()) {
                String likeLocation = "%" + request.getMainDestination().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("mainDestination")), likeLocation));
            }

            if (request.getDate() != null) {
                predicates.add(cb.equal(root.join("departures").get("departureDate"), request.getDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
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
