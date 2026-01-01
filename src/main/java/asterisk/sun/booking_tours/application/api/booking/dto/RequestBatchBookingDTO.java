package asterisk.sun.booking_tours.application.api.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating multiple bookings at once (batch booking).
 * This will create 5 bookings using available tour departures.
 */
public class RequestBatchBookingDTO {

    @NotNull(message = "Number of adults per booking is required")
    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer numAdultsPerBooking;

    @Min(value = 0, message = "Number of children must be at least 0")
    private Integer numChildPerBooking;

    @NotNull(message = "Contact name is required")
    @Size(min = 1, max = 100, message = "Contact name must be between 1 and 100 characters")
    private String contactName;

    @NotNull(message = "Contact phone is required")
    private String contactPhone;

    @NotNull(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public RequestBatchBookingDTO() {
    }

    public Integer getNumAdultsPerBooking() {
        return numAdultsPerBooking;
    }

    public void setNumAdultsPerBooking(Integer numAdultsPerBooking) {
        this.numAdultsPerBooking = numAdultsPerBooking;
    }

    public Integer getNumChildPerBooking() {
        return numChildPerBooking;
    }

    public void setNumChildPerBooking(Integer numChildPerBooking) {
        this.numChildPerBooking = numChildPerBooking;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
