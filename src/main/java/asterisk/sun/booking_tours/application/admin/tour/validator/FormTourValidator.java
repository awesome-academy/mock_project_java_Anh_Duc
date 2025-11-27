package asterisk.sun.booking_tours.application.admin.tour.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import asterisk.sun.booking_tours.application.admin.tour.TourAdminService;
import asterisk.sun.booking_tours.application.admin.tour.dto.FormCreateTourDTO;
import asterisk.sun.booking_tours.application.admin.tour.dto.FormEditTourDTO;

@Component
public class FormTourValidator implements Validator {
    private final TourAdminService service;

    public FormTourValidator(TourAdminService tourService) {
        this.service = tourService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormCreateTourDTO.class.equals(clazz) || FormEditTourDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof FormCreateTourDTO) {
            validateCreateForm((FormCreateTourDTO) target, errors);
        } else if (target instanceof FormEditTourDTO) {
            validateEditForm((FormEditTourDTO) target, errors);
        }
    }

    private void validateCreateForm(FormCreateTourDTO dto, Errors errors) {
        // Validate tour name uniqueness
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            if (service.existsByName(dto.getName())) {
                errors.rejectValue("name", "error.tour", "Tour name already exists");
            }
        }

        // Validate slug uniqueness
        if (dto.getSlug() != null && !dto.getSlug().isEmpty()) {
            if (service.existsBySlug(dto.getSlug())) {
                errors.rejectValue("slug", "error.tour", "Tour slug already exists");
            }
        }

        // Validate duration
        if (dto.getDurationDays() != null && dto.getDurationNights() != null) {
            if (dto.getDurationNights() >= dto.getDurationDays()) {
                errors.rejectValue("durationNights", "error.tour",
                    "Duration nights should be less than duration days");
            }
        }

        // Validate prices
        if (dto.getPriceAdult() != null && dto.getPriceChild() != null) {
            if (dto.getPriceChild().compareTo(dto.getPriceAdult()) > 0) {
                errors.rejectValue("priceChild", "error.tour",
                    "Child price should not be greater than adult price");
            }
        }
    }

    private void validateEditForm(FormEditTourDTO dto, Errors errors) {
        // Validate tour name uniqueness (excluding current tour)
        if (dto.getName() != null && !dto.getName().isEmpty() && dto.getId() != null) {
            if (service.existsByNameExcludingId(dto.getName(), dto.getId())) {
                errors.rejectValue("name", "error.tour", "Tour name already exists");
            }
        }

        // Validate slug uniqueness (excluding current tour)
        if (dto.getSlug() != null && !dto.getSlug().isEmpty() && dto.getId() != null) {
            if (service.existsBySlugExcludingId(dto.getSlug(), dto.getId())) {
                errors.rejectValue("slug", "error.tour", "Tour slug already exists");
            }
        }

        // Validate duration
        if (dto.getDurationDays() != null && dto.getDurationNights() != null) {
            if (dto.getDurationNights() >= dto.getDurationDays()) {
                errors.rejectValue("durationNights", "error.tour",
                    "Duration nights should be less than duration days");
            }
        }

        // Validate prices
        if (dto.getPriceAdult() != null && dto.getPriceChild() != null) {
            if (dto.getPriceChild().compareTo(dto.getPriceAdult()) > 0) {
                errors.rejectValue("priceChild", "error.tour",
                    "Child price should not be greater than adult price");
            }
        }
    }
}
