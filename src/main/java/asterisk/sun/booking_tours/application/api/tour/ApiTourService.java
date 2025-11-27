package asterisk.sun.booking_tours.application.api.tour;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public List<ListToursResponseDTO> getListTours(SearchToursRequestDTO param) {
        if (param.getKeyword() != null) {
            List<Tour> tours = tourRepository.searchByKeyword(param.getKeyword());
            return MapperHelper.mapList(tours, ListToursResponseDTO.class);
        }

        if (param.getMainDestination() != null) {
            List<Tour> tours = tourRepository.searchByLocation(param.getMainDestination());
            return MapperHelper.mapList(tours, ListToursResponseDTO.class);
        }

        if (param.getDate() != null) {
            List<Tour> tours = tourRepository.searchByDate(param.getDate());
            return MapperHelper.mapList(tours, ListToursResponseDTO.class);
        }

        List<Tour> tours = tourRepository.findAll();
        return MapperHelper.mapList(tours, ListToursResponseDTO.class);
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
