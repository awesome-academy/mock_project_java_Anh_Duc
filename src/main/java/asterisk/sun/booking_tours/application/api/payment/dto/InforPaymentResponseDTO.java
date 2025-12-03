package asterisk.sun.booking_tours.application.api.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import asterisk.sun.booking_tours.core.payment.BankAccount;
import groovyjarjarpicocli.CommandLine.Help.Ansi.Text;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InforPaymentResponseDTO {
    private LocalDate departureDate;
    private LocalDate returnDate;
    private Integer durationDays;
    private Integer numAdults;
    private Integer numChildren;
    private BigDecimal finalTotal;
    private String notes;
    private String contactName;
    private String contactPhone;
    private String tourName;
    private String tourDescription;
    private List<BankAccount> bankAccounts;
}
