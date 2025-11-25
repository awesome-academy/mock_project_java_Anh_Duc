package asterisk.sun.booking_tours.application.admin.tourdepartures.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import asterisk.sun.booking_tours.core.tourdepartures.TourDepartureStatus;

public class FormEditTourDeparturesDTO {

    private Long id;

    @NotNull(message = "Tour is required")
    private Long tourId;

    @NotNull(message = "Status is required")
    private TourDepartureStatus status;

    @NotNull(message = "Departure date is required")
    private LocalDate departureDate;

    @NotNull(message = "Return date is required")
    private LocalDate returnDate;

    @NotNull(message = "Total slots is required")
    @Min(value = 1, message = "Total slots must be at least 1")
    private Integer totalSlots;

    @NotNull(message = "Available slots is required")
    @Min(value = 0, message = "Available slots must be at least 0")
    private Integer availableSlots;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    public TourDepartureStatus getStatus() {
        return status;
    }

    public void setStatus(TourDepartureStatus status) {
        this.status = status;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(Integer totalSlots) {
        this.totalSlots = totalSlots;
    }

    public Integer getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }
}
