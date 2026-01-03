package asterisk.sun.booking_tours.application.rest.admin.report.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import asterisk.sun.booking_tours.core.report.ReportType;

public class ReportRequestMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reportId;
    private String reportCode;
    private ReportType reportType;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;

    private Long requestedByUserId;

    public ReportRequestMessage() {}

    public ReportRequestMessage(Long reportId, String reportCode, ReportType reportType,
                                LocalDate startDate, LocalDate endDate, Long requestedByUserId) {
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestedByUserId = requestedByUserId;
    }

    // Getters and Setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

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

    public Long getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(Long requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    @Override
    public String toString() {
        return "ReportRequestMessage{" +
                "reportId=" + reportId +
                ", reportCode='" + reportCode + '\'' +
                ", reportType=" + reportType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", requestedByUserId=" + requestedByUserId +
                '}';
    }
}
