package asterisk.sun.booking_tours.application.api.booking.dto;

import jakarta.validation.constraints.NotNull;
public class RequestCancelBookingDTO {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Cancellation reason is required")
    private String reason;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
