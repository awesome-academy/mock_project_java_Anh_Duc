package asterisk.sun.booking_tours.application.admin.tourdepartures.validator;

import asterisk.sun.booking_tours.application.admin.tourdepartures.TourDeparturesAdminService;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.FormCreateTourDeparturesDTO;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.FormEditTourDeparturesDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FormTourDeparturesValidator implements Validator {

    private final TourDeparturesAdminService tourDeparturesAdminService;

    public FormTourDeparturesValidator(TourDeparturesAdminService tourDeparturesAdminService) {
        this.tourDeparturesAdminService = tourDeparturesAdminService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormCreateTourDeparturesDTO.class.equals(clazz) || FormEditTourDeparturesDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof FormCreateTourDeparturesDTO) {
            validateCreate((FormCreateTourDeparturesDTO) target, errors);
        } else if (target instanceof FormEditTourDeparturesDTO) {
            validateEdit((FormEditTourDeparturesDTO) target, errors);
        }
    }

    private void validateCreate(FormCreateTourDeparturesDTO dto, Errors errors) {
        // Validate return date is after departure date
        if (dto.getDepartureDate() != null && dto.getReturnDate() != null) {
            if (dto.getReturnDate().isBefore(dto.getDepartureDate()) ||
                    dto.getReturnDate().isEqual(dto.getDepartureDate())) {
                errors.rejectValue("returnDate", "returnDate.invalid",
                        "Return date must be after departure date");
            }
        }

        // Validate available slots not exceeding total slots
        if (dto.getTotalSlots() != null && dto.getAvailableSlots() != null) {
            if (dto.getAvailableSlots() > dto.getTotalSlots()) {
                errors.rejectValue("availableSlots", "availableSlots.exceeds",
                        "Available slots cannot exceed total slots");
            }
        }

        // Validate unique tour and departure date combination
        if (dto.getTourId() != null && dto.getDepartureDate() != null) {
            if (tourDeparturesAdminService.existsByTourIdAndDepartureDate(dto.getTourId(), dto.getDepartureDate())) {
                errors.rejectValue("departureDate", "departureDate.duplicate",
                        "A departure already exists for this tour on the selected date");
            }
        }
    }

    private void validateEdit(FormEditTourDeparturesDTO dto, Errors errors) {
        // Validate return date is after departure date
        if (dto.getDepartureDate() != null && dto.getReturnDate() != null) {
            if (dto.getReturnDate().isBefore(dto.getDepartureDate()) ||
                    dto.getReturnDate().isEqual(dto.getDepartureDate())) {
                errors.rejectValue("returnDate", "returnDate.invalid",
                        "Return date must be after departure date");
            }
        }

        // Validate available slots not exceeding total slots
        if (dto.getTotalSlots() != null && dto.getAvailableSlots() != null) {
            if (dto.getAvailableSlots() > dto.getTotalSlots()) {
                errors.rejectValue("availableSlots", "availableSlots.exceeds",
                        "Available slots cannot exceed total slots");
            }
        }

        // Validate unique tour and departure date combination (excluding current
        // record)
        if (dto.getId() != null && dto.getTourId() != null && dto.getDepartureDate() != null) {
            if (tourDeparturesAdminService.existsByTourIdAndDepartureDateExcludingId(
                    dto.getTourId(), dto.getDepartureDate(), dto.getId())) {
                errors.rejectValue("departureDate", "departureDate.duplicate",
                        "A departure already exists for this tour on the selected date");
            }
        }
    }
}
