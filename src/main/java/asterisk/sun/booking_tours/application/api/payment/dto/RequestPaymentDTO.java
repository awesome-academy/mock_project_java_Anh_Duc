package asterisk.sun.booking_tours.application.api.payment.dto;

import java.math.BigDecimal;

import asterisk.sun.booking_tours.common.dto.BaseDTO;
import asterisk.sun.booking_tours.core.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPaymentDTO {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Size(max = 100, message = "Bank code cannot exceed 100 characters")
    private String bankCode;

    @Size(max = 100, message = "Account number cannot exceed 100 characters")
    private String accountNumber;

    @Size(max = 200, message = "Account name cannot exceed 200 characters")
    private String accountName;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
