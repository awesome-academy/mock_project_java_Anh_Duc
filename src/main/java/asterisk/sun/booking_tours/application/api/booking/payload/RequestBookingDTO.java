package asterisk.sun.booking_tours.application.api.booking.payload;

import asterisk.sun.booking_tours.common.dto.BaseDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RequestBookingDTO extends BaseDTO {
    @NotNull(message = "Tour departure ID is required")
    private Long tourDepartureId;

    @NotNull(message = "User ID is required")
    private Long userId;


    @NotNull(message = "Number of adults is required")
    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer numAdults;

    @Min(value = 0, message = "Number of children must be at least 0")
    private Integer numChild;

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

    private String couponCode;

    public RequestBookingDTO() {
    }

    public Long getTourDepartureId() {
        return tourDepartureId;
    }
    public void setTourDepartureId(Long tourDepartureId) {
        this.tourDepartureId = tourDepartureId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
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
    public String getCouponCode() {
        return couponCode;
    }
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

}
