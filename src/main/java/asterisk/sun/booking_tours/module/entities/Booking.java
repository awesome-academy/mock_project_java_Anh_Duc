package asterisk.sun.booking_tours.module.entities;

import java.math.BigDecimal;

import asterisk.sun.booking_tours.module.common.entities.BaseEntity;
import asterisk.sun.booking_tours.module.enums.BookingStatus;
import asterisk.sun.booking_tours.module.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_departure_id", referencedColumnName = "id")
    private TourDepartures tourDeparture;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "num_adults", nullable = false)
    private Integer numAdults;

    @Column(name = "num_child", nullable = false)
    private Integer numChild;

    @Column(name = "sub_total", precision = 15, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "discount", precision = 15, scale = 2)
    private BigDecimal discount;

    @Column(name = "final_total", precision = 15, scale = 2)
    private BigDecimal finalTotal;

    @Column(name = "contact_name", length = 255, nullable = false)
    private String contactName;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    // Constructors
    public Booking() {}

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TourDepartures getTourDeparture() {
        return tourDeparture;
    }

    public void setTourDeparture(TourDepartures tourDeparture) {
        this.tourDeparture = tourDeparture;
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
