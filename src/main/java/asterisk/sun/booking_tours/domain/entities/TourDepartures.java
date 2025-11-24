package asterisk.sun.booking_tours.domain.entities;

import java.time.LocalDate;

import asterisk.sun.booking_tours.domain.common.abstracts.BaseEntity;
import asterisk.sun.booking_tours.domain.enums.TourDepartureStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tour_departures")
public class TourDepartures extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", referencedColumnName = "id", nullable = false)
    private Tour tour;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TourDepartureStatus status;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "available_slots")
    private Integer availableSlots;

    // Constructors
    public TourDepartures() {}

    // Getters and Setters
    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
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
