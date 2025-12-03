package asterisk.sun.booking_tours.application.api.payment.dto;

import asterisk.sun.booking_tours.core.payment.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdatePaymentStatusDTO {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @NotNull(message = "Status is required")
    private PaymentStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public UpdatePaymentStatusDTO() {}

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
