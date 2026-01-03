package asterisk.sun.booking_tours.core.report;

import java.time.LocalDate;
import java.time.LocalDateTime;

import asterisk.sun.booking_tours.core.BaseEntity;
import asterisk.sun.booking_tours.core.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "revenue_reports")
public class RevenueReport extends BaseEntity {

    @Column(name = "report_code", unique = true, nullable = false)
    private String reportCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", referencedColumnName = "id")
    private User requestedBy;

    // Constructors
    public RevenueReport() {}

    public RevenueReport(String reportCode, ReportType reportType, LocalDate startDate, LocalDate endDate, User requestedBy) {
        this.reportCode = reportCode;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestedBy = requestedBy;
        this.status = ReportStatus.PENDING;
    }

    // Getters and Setters
    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }
}
