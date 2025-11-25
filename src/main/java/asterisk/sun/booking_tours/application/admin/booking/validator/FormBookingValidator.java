package asterisk.sun.booking_tours.application.admin.booking.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import asterisk.sun.booking_tours.application.admin.booking.BookingAdminService;
import asterisk.sun.booking_tours.application.admin.booking.dto.FormCreateBookingDTO;
import asterisk.sun.booking_tours.application.admin.booking.dto.FormEditBookingDTO;

@Component
public class FormBookingValidator implements Validator {

    private final BookingAdminService service;

    public FormBookingValidator(BookingAdminService bookingAdminService) {
        this.service = bookingAdminService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormCreateBookingDTO.class.equals(clazz) || FormEditBookingDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof FormCreateBookingDTO) {
            validateCreateForm((FormCreateBookingDTO) target, errors);
        } else if (target instanceof FormEditBookingDTO) {
            validateEditForm((FormEditBookingDTO) target, errors);
        }
    }

    private void validateCreateForm(FormCreateBookingDTO dto, Errors errors) {
        // Validate total calculation
        validateTotalCalculation(dto.getSubTotal(), dto.getDiscount(), dto.getFinalTotal(), errors);

        // Validate number of participants
        validateParticipants(dto.getNumAdults(), dto.getNumChild(), errors);

        // Validate contact information
        validateContactInfo(dto.getContactPhone(), dto.getContactEmail(), errors);
    }

    private void validateEditForm(FormEditBookingDTO dto, Errors errors) {
        // Validate total calculation
        validateTotalCalculation(dto.getSubTotal(), dto.getDiscount(), dto.getFinalTotal(), errors);

        // Validate number of participants
        validateParticipants(dto.getNumAdults(), dto.getNumChild(), errors);

        // Validate contact information
        validateContactInfo(dto.getContactPhone(), dto.getContactEmail(), errors);
    }

    /**
     * Validate that final total = sub total - discount
     */
    private void validateTotalCalculation(BigDecimal subTotal, BigDecimal discount, BigDecimal finalTotal, Errors errors) {
        if (subTotal != null && discount != null && finalTotal != null) {
            BigDecimal expectedFinalTotal = subTotal.subtract(discount);
            if (finalTotal.compareTo(expectedFinalTotal) != 0) {
                errors.rejectValue("finalTotal", "error.booking",
                    "Final total must equal sub total minus discount");
            }
        }

        // Validate discount not greater than sub total
        if (subTotal != null && discount != null) {
            if (discount.compareTo(subTotal) > 0) {
                errors.rejectValue("discount", "error.booking",
                    "Discount cannot be greater than sub total");
            }
        }
    }

    /**
     * Validate number of participants
     */
    private void validateParticipants(Integer numAdults, Integer numChild, Errors errors) {
        if (numAdults != null && numChild != null) {
            if (numAdults == 0 && numChild == 0) {
                errors.rejectValue("numAdults", "error.booking",
                    "At least one adult or child is required");
            }
        }
    }

    /**
     * Validate contact information format
     */
    private void validateContactInfo(String contactPhone, String contactEmail, Errors errors) {
        // Validate phone number format (basic validation)
        if (contactPhone != null && !contactPhone.isEmpty()) {
            if (!contactPhone.matches("^[0-9+\\-\\s()]+$")) {
                errors.rejectValue("contactPhone", "error.booking",
                    "Invalid phone number format");
            }
        }
    }
}
