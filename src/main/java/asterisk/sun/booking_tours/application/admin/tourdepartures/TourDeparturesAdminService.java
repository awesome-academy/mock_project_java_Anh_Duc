package asterisk.sun.booking_tours.application.admin.tourdepartures;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.FormCreateTourDeparturesDTO;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.FormEditTourDeparturesDTO;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.ListTourDeparturesDTO;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.SearchTourDeparturesDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TourDeparturesAdminService extends BaseServiceController<TourDeparturesRepository> {

    private final TourRepository tourRepository;

    public TourDeparturesAdminService(TourDeparturesRepository tourDeparturesRepository,
            TourRepository tourRepository) {
        super(tourDeparturesRepository);
        this.tourRepository = tourRepository;
    }

    public Page<ListTourDeparturesDTO> queryTourDeparturesByKeyword(SearchTourDeparturesDTO request) {
        Pageable pageable = request.getPageable();

        Specification<TourDeparture> spec = (root, query, cb) -> {
            // To avoid duplicates when using fetch joins
            query.distinct(true);

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likeKey = "%" + request.getKeyword().toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("status").as(String.class)), likeKey),
                        cb.like(cb.lower(root.join("tour").get("name")), likeKey),
                        cb.like(cb.lower(root.join("tour").get("title")), likeKey),
                        cb.like(cb.lower(root.join("tour").join("category").get("name")), likeKey)
                );
            }
            return cb.conjunction();
        };

        Page<TourDeparture> pageResult = repository.findAll(spec, pageable);

        return pageResult.map(td -> {
            ListTourDeparturesDTO dto = MapperHelper.map(td, ListTourDeparturesDTO.class);
            dto.setStatus(td.getStatus());
            if (td.getTour() != null) {
                dto.setTourId(td.getTour().getId());
                dto.setTourName(td.getTour().getName());
                dto.setTourTitle(td.getTour().getTitle());
                if (td.getTour().getCategory() != null) {
                    dto.setCategoryName(td.getTour().getCategory().getName());
                }
            }
            return dto;
        });
    }

    public void createTourDeparture(FormCreateTourDeparturesDTO formCreateTourDeparturesDTO) {
        TourDeparture tourDeparture = new TourDeparture();
        tourDeparture.setStatus(formCreateTourDeparturesDTO.getStatus());
        tourDeparture.setDepartureDate(formCreateTourDeparturesDTO.getDepartureDate());
        tourDeparture.setReturnDate(formCreateTourDeparturesDTO.getReturnDate());
        tourDeparture.setTotalSlots(formCreateTourDeparturesDTO.getTotalSlots());
        tourDeparture.setAvailableSlots(formCreateTourDeparturesDTO.getAvailableSlots());

        // Set tour
        Tour tour = tourRepository.findById(formCreateTourDeparturesDTO.getTourId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tour not found with id: " + formCreateTourDeparturesDTO.getTourId()));
        tourDeparture.setTour(tour);

        repository.save(tourDeparture);
    }

    public FormEditTourDeparturesDTO getTourDepartureById(Long id) {
        TourDeparture tourDeparture = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour Departure not found with id: " + id));

        FormEditTourDeparturesDTO dto = MapperHelper.map(tourDeparture, FormEditTourDeparturesDTO.class);
        if (tourDeparture.getTour() != null) {
            dto.setTourId(tourDeparture.getTour().getId());
        }
        return dto;
    }

    public void updateTourDeparture(FormEditTourDeparturesDTO formEditTourDeparturesDTO) {
        TourDeparture tourDeparture = repository.findById(formEditTourDeparturesDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tour Departure not found with id: " + formEditTourDeparturesDTO.getId()));

        tourDeparture.setStatus(formEditTourDeparturesDTO.getStatus());
        tourDeparture.setDepartureDate(formEditTourDeparturesDTO.getDepartureDate());
        tourDeparture.setReturnDate(formEditTourDeparturesDTO.getReturnDate());
        tourDeparture.setTotalSlots(formEditTourDeparturesDTO.getTotalSlots());
        tourDeparture.setAvailableSlots(formEditTourDeparturesDTO.getAvailableSlots());

        // Update tour if changed
        Tour tour = tourRepository.findById(formEditTourDeparturesDTO.getTourId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tour not found with id: " + formEditTourDeparturesDTO.getTourId()));
        tourDeparture.setTour(tour);

        repository.save(tourDeparture);
    }

    public void deleteTourDeparture(Long id) {
        TourDeparture tourDeparture = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour Departure not found with id: " + id));

        repository.delete(tourDeparture);
    }

    public boolean existsByTourIdAndDepartureDate(Long tourId, LocalDate departureDate) {
        return repository.existsByTourIdAndDepartureDate(tourId, departureDate);
    }

    public boolean existsByTourIdAndDepartureDateExcludingId(Long tourId, LocalDate departureDate, Long id) {
        return repository.findByTourId(tourId).stream()
                .anyMatch(td -> td.getDepartureDate().equals(departureDate) && !td.getId().equals(id));
    }
}
