package asterisk.sun.booking_tours.application.admin.booking.dto;

import java.math.BigDecimal;

import asterisk.sun.booking_tours.core.booking.BookingStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for editing an existing booking in admin panel
 */
public class FormEditBookingDTO {

    @NotNull(message = "ID is required")
    private Long id;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Tour departure is required")
    private Long tourDepartureId;

    @NotNull(message = "Status is required")
    private BookingStatus status;

    private String notes;

    @NotNull(message = "Number of adults is required")
    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer numAdults;

    @NotNull(message = "Number of children is required")
    @Min(value = 0, message = "Number of children must be at least 0")
    private Integer numChild;

    @NotNull(message = "Sub total is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sub total must be greater than 0")
    private BigDecimal subTotal;

    @DecimalMin(value = "0.0", message = "Discount must be at least 0")
    private BigDecimal discount;

    @NotNull(message = "Final total is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Final total must be greater than 0")
    private BigDecimal finalTotal;

    @NotBlank(message = "Contact name is required")
    private String contactName;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    // Constructors
    public FormEditBookingDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTourDepartureId() {
        return tourDepartureId;
    }

    public void setTourDepartureId(Long tourDepartureId) {
        this.tourDepartureId = tourDepartureId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(Integer numAdults) {
        this.numAdults = numAdults;
    }

    public Integer getNumChild() {
        return numChild;
    }

    public void setNumChild(Integer numChild) {
        this.numChild = numChild;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
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
}
