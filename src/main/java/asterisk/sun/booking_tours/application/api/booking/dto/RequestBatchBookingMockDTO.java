package asterisk.sun.booking_tours.application.api.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating multiple bookings at once (batch booking) using mock data from users in database.
 * Contact information will be automatically retrieved from random users in the system.
 */
public class RequestBatchBookingMockDTO {

    @Min(value = 1, message = "Number of bookings must be at least 1")
    private Integer numberOfBookings = 5;

    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer numAdultsPerBooking = 1;

    @Min(value = 0, message = "Number of children must be at least 0")
    private Integer numChildPerBooking = 0;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public RequestBatchBookingMockDTO() {
    }

    public Integer getNumberOfBookings() {
        return numberOfBookings;
    }

    public void setNumberOfBookings(Integer numberOfBookings) {
        this.numberOfBookings = numberOfBookings;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
