package asterisk.sun.booking_tours.application.rest.admin.report.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import asterisk.sun.booking_tours.core.report.ReportStatus;
import asterisk.sun.booking_tours.core.report.ReportType;

public class ReportResponseDTO {

    private Long id;
    private String reportCode;
    private ReportType reportType;
    private String reportTypeDescription;
    private ReportStatus status;
    private String statusLabel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String fileName;
    private Long fileSize;
    private String errorMessage;
    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
    private String requestedByUsername;

    public ReportResponseDTO() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ReportResponseDTO dto = new ReportResponseDTO();

        public Builder id(Long id) {
            dto.id = id;
            return this;
        }

        public Builder reportCode(String reportCode) {
            dto.reportCode = reportCode;
            return this;
        }

        public Builder reportType(ReportType reportType) {
            dto.reportType = reportType;
            dto.reportTypeDescription = reportType != null ? reportType.getDescription() : null;
            return this;
        }

        public Builder status(ReportStatus status) {
            dto.status = status;
            dto.statusLabel = status != null ? status.getLabel() : null;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            dto.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            dto.endDate = endDate;
            return this;
        }

        public Builder fileName(String fileName) {
            dto.fileName = fileName;
            return this;
        }

        public Builder fileSize(Long fileSize) {
            dto.fileSize = fileSize;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            dto.errorMessage = errorMessage;
            return this;
        }

        public Builder generatedAt(LocalDateTime generatedAt) {
            dto.generatedAt = generatedAt;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            dto.createdAt = createdAt;
            return this;
        }

        public Builder requestedByUsername(String requestedByUsername) {
            dto.requestedByUsername = requestedByUsername;
            return this;
        }

        public ReportResponseDTO build() {
            return dto;
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getReportCode() {
        return reportCode;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public String getReportTypeDescription() {
        return reportTypeDescription;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getRequestedByUsername() {
        return requestedByUsername;
    }
}
