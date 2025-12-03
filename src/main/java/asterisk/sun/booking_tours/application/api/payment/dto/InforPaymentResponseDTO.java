package asterisk.sun.booking_tours.application.api.payment.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InforPaymentResponseDTO {
    private Long paymentId;
    private String paymentUrl;
}
