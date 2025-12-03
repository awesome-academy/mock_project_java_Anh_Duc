package asterisk.sun.booking_tours.application.api.payment.dto;

import java.math.BigDecimal;

import asterisk.sun.booking_tours.core.payment.PaymentMethod;
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
    @NotNull(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 100, message = "Bank code cannot exceed 100 characters")
    private String bankCode;
}
