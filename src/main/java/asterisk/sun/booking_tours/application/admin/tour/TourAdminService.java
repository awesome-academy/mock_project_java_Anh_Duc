package asterisk.sun.booking_tours.application.admin.tour;

import java.util.List;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.tour.dto.FormCreateTourDTO;
import asterisk.sun.booking_tours.application.admin.tour.dto.FormEditTourDTO;
import asterisk.sun.booking_tours.application.admin.tour.dto.ListTourDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.category.Category;
import asterisk.sun.booking_tours.core.category.CategoryRepository;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class TourAdminService extends BaseServiceController<TourRepository> {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TourAdminService(TourRepository tourRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        super(tourRepository);
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<ListTourDTO> queryToursByKeyword(String keyword) {
        List<Tour> tours = repository.searchByKeyword(keyword);

        return tours.stream()
                .map(tour -> {
                    ListTourDTO dto = MapperHelper.map(tour, ListTourDTO.class);
                    if (tour.getCategory() != null) {
                        dto.setCategoryName(tour.getCategory().getName());
                        dto.setCategoryId(tour.getCategory().getId());
                    }
                    if (tour.getCreator() != null) {
                        dto.setCreatorUsername(tour.getCreator().getUsername());
                        dto.setCreatorId(tour.getCreator().getId());
                    }
                    return dto;
                })
                .toList();
    }

    public void createTour(FormCreateTourDTO formCreateTourDTO) {
        Tour tour = new Tour();
        tour.setName(formCreateTourDTO.getName());
        tour.setTitle(formCreateTourDTO.getTitle());
        tour.setDescription(formCreateTourDTO.getDescription());
        tour.setSlug(formCreateTourDTO.getSlug());
        tour.setPrice(formCreateTourDTO.getPrice());
        tour.setThumbnailUrl(formCreateTourDTO.getThumbnailUrl());
        tour.setDepartureLocation(formCreateTourDTO.getDepartureLocation());
        tour.setMainDestination(formCreateTourDTO.getMainDestination());
        tour.setItinerary(formCreateTourDTO.getItinerary());
        tour.setDurationDays(formCreateTourDTO.getDurationDays());
        tour.setDurationNights(formCreateTourDTO.getDurationNights());
        tour.setPriceAdult(formCreateTourDTO.getPriceAdult());
        tour.setPriceChild(formCreateTourDTO.getPriceChild());
        tour.setCurrency(formCreateTourDTO.getCurrency());
        tour.setTermsAndConditions(formCreateTourDTO.getTermsAndConditions());

        // Set category
        Category category = categoryRepository.findById(formCreateTourDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + formCreateTourDTO.getCategoryId()));
        tour.setCategory(category);

        // Set creator
        User creator = userRepository.findById(formCreateTourDTO.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + formCreateTourDTO.getCreatorId()));
        tour.setCreator(creator);

        repository.save(tour);
    }

    public FormEditTourDTO getTourById(Long id) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + id));

        FormEditTourDTO dto = MapperHelper.map(tour, FormEditTourDTO.class);
        if (tour.getCategory() != null) {
            dto.setCategoryId(tour.getCategory().getId());
        }
        if (tour.getCreator() != null) {
            dto.setCreatorId(tour.getCreator().getId());
        }
        return dto;
    }

    public void updateTour(FormEditTourDTO formEditTourDTO) {
        Tour tour = repository.findById(formEditTourDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tour not found with id: " + formEditTourDTO.getId()));

        tour.setName(formEditTourDTO.getName());
        tour.setTitle(formEditTourDTO.getTitle());
        tour.setDescription(formEditTourDTO.getDescription());
        tour.setSlug(formEditTourDTO.getSlug());
        tour.setPrice(formEditTourDTO.getPrice());
        tour.setThumbnailUrl(formEditTourDTO.getThumbnailUrl());
        tour.setDepartureLocation(formEditTourDTO.getDepartureLocation());
        tour.setMainDestination(formEditTourDTO.getMainDestination());
        tour.setItinerary(formEditTourDTO.getItinerary());
        tour.setDurationDays(formEditTourDTO.getDurationDays());
        tour.setDurationNights(formEditTourDTO.getDurationNights());
        tour.setPriceAdult(formEditTourDTO.getPriceAdult());
        tour.setPriceChild(formEditTourDTO.getPriceChild());
        tour.setCurrency(formEditTourDTO.getCurrency());
        tour.setTermsAndConditions(formEditTourDTO.getTermsAndConditions());

        // Update category
        Category category = categoryRepository.findById(formEditTourDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + formEditTourDTO.getCategoryId()));
        tour.setCategory(category);

        // Update creator
        User creator = userRepository.findById(formEditTourDTO.getCreatorId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + formEditTourDTO.getCreatorId()));
        tour.setCreator(creator);

        repository.save(tour);
    }

    public void deleteTour(Long id) {
        Tour tour = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id: " + id));

        repository.delete(tour);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    public boolean existsByNameExcludingId(String name, Long id) {
        return repository.findByName(name)
                .map(tour -> !tour.getId().equals(id))
                .orElse(false);
    }

    public boolean existsBySlugExcludingId(String slug, Long id) {
        return repository.findBySlug(slug)
                .map(tour -> !tour.getId().equals(id))
                .orElse(false);
    }
}
