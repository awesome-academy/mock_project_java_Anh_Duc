package asterisk.sun.booking_tours.application.rest.admin.report.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import asterisk.sun.booking_tours.core.report.ReportStatus;
import asterisk.sun.booking_tours.core.report.ReportType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
    private Long id;
    private String reportCode;
    private ReportType reportType;
    private ReportStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String fileName;
    private String errorMessage;
    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
}
