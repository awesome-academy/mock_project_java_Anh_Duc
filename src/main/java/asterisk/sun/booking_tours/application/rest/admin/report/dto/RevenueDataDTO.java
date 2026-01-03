package asterisk.sun.booking_tours.application.rest.admin.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for revenue data in reports
 */
public class RevenueDataDTO {

    private LocalDate date;
    private Long totalBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private BigDecimal totalRevenue;
    private BigDecimal averageBookingValue;
    private String tourName;
    private String categoryName;

    public RevenueDataDTO() {}

    public RevenueDataDTO(LocalDate date, Long totalBookings, Long completedBookings,
                          Long cancelledBookings, BigDecimal totalRevenue) {
        this.date = date;
        this.totalBookings = totalBookings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
        this.totalRevenue = totalRevenue;
        if (completedBookings != null && completedBookings > 0 && totalRevenue != null) {
            this.averageBookingValue = totalRevenue.divide(BigDecimal.valueOf(completedBookings), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Long getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(Long completedBookings) {
        this.completedBookings = completedBookings;
    }

    public Long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(Long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getAverageBookingValue() {
        return averageBookingValue;
    }

    public void setAverageBookingValue(BigDecimal averageBookingValue) {
        this.averageBookingValue = averageBookingValue;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
