package asterisk.sun.booking_tours.application.rest.admin.report.dto;

import java.time.LocalDate;

import asterisk.sun.booking_tours.core.report.ReportType;
import jakarta.validation.constraints.NotNull;

public class CreateReportRequestDTO {

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public CreateReportRequestDTO() {}

    public CreateReportRequestDTO(ReportType reportType, LocalDate startDate, LocalDate endDate) {
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
