package asterisk.sun.booking_tours.application.rest.admin.report;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.api.common.dto.PaginatedResponse;
import asterisk.sun.booking_tours.application.api.common.dto.SuccessResponse;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.CreateReportRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.report.ReportStatus;
import asterisk.sun.booking_tours.core.report.ReportType;
import asterisk.sun.booking_tours.core.report.RevenueReport;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/reports")
@Tag(name = "Admin Report", description = "Admin API for revenue report management with JMS/Queue processing")
public class ApiAdminReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a new revenue report request", description = "Creates a report request that will be processed asynchronously via JMS queue. "
            +
            "The report will be generated in the background and you can check its status using the report code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Report request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<SuccessResponse<ReportResponseDTO>> createReport(
            @Valid @RequestBody CreateReportRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ReportResponseDTO response = reportService.createReportRequest(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(
                        HttpStatus.CREATED.value(),
                        "Report request created successfully. Report Code: " + response.getReportCode(),
                        response));
    }

    @GetMapping("/{reportCode}")
    @Operation(summary = "Get report by code", description = "Retrieve report details and status by report code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<SuccessResponse<ReportResponseDTO>> getReport(
            @Parameter(description = "Report code") @PathVariable String reportCode) {

        ReportResponseDTO response = reportService.getReportByCode(reportCode);

        return ResponseEntity.ok(new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Report retrieved successfully",
                response));
    }

    @GetMapping("/{reportCode}/status")
    @Operation(summary = "Check report generation status", description = "Check if the report has been generated and is ready for download")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<SuccessResponse<ReportStatusResponse>> getReportStatus(
            @Parameter(description = "Report code") @PathVariable String reportCode) {

        ReportResponseDTO report = reportService.getReportByCode(reportCode);

        ReportStatusResponse statusResponse = new ReportStatusResponse(
                report.getReportCode(),
                report.getStatus(),
                report.getStatus() == ReportStatus.COMPLETED,
                report.getFileName(),
                report.getErrorMessage());

        return ResponseEntity.ok(new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Report status retrieved successfully",
                statusResponse));
    }

    @GetMapping("/{reportCode}/download")
    @Operation(summary = "Download report file", description = "Download the generated Excel report file. Report must be in COMPLETED status. File is generated on-the-fly when downloading.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "400", description = "Report not ready for download"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<Resource> downloadReport(
            @Parameter(description = "Report code") @PathVariable String reportCode) throws IOException {

        // Get report entity to validate status
        RevenueReport report = reportService.getReportForDownload(reportCode);

        if (report.getStatus() != ReportStatus.COMPLETED) {
            throw new RuntimeException("Report is not ready for download. Current status: " + report.getStatus());
        }

        // Generate report file on-the-fly
        byte[] fileContent = reportService.generateReportForDownload(reportCode);
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        // Get filename
        String fileName = reportService.getReportFileName(reportCode);

        // Set response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping
    @Operation(summary = "Get all reports with pagination", description = "Retrieve list of all reports with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    public ResponseEntity<PaginatedResponse<ReportResponseDTO>> getAllReports(ReportRequestDTO request) {
        Page<RevenueReport> reports = reportService.getAllReports(request);
        List<ReportResponseDTO> data = MapperHelper.mapList(reports.getContent(), ReportResponseDTO.class);

        PaginatedResponse<ReportResponseDTO> response = new PaginatedResponse<>(
                HttpStatus.OK.value(),
                "Reports retrieved successfully",
                data,
                reports.getTotalElements(),
                reports.getNumber() + 1,
                reports.getSize());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reportCode}/cancel")
    @Operation(summary = "Cancel a report request", description = "Cancel a report that is in PENDING or PROCESSING status. Once cancelled, the report will not be generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Report cannot be cancelled (already completed/failed/cancelled)"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<SuccessResponse<ReportResponseDTO>> cancelReport(
            @Parameter(description = "Report code") @PathVariable String reportCode) {

        ReportResponseDTO response = reportService.cancelReport(reportCode);

        return ResponseEntity.ok(new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Report cancelled successfully",
                response));
    }

    @DeleteMapping("/{reportCode}")
    @Operation(summary = "Delete a report", description = "Delete a report that is in COMPLETED, FAILED, or CANCELLED status. Reports that are still being processed cannot be deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Report cannot be deleted (still processing)"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    public ResponseEntity<SuccessResponse<Void>> deleteReport(
            @Parameter(description = "Report code") @PathVariable String reportCode) {

        reportService.deleteReport(reportCode);

        return ResponseEntity.ok(new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Report deleted successfully",
                null));
    }

    @GetMapping("/types")
    @Operation(summary = "Get available report types", description = "Retrieve list of all available report types")
    public ResponseEntity<SuccessResponse<ReportType[]>> getReportTypes() {
        return ResponseEntity.ok(new SuccessResponse<>(
                HttpStatus.OK.value(),
                "Report types retrieved successfully",
                ReportType.values()));
    }

    /**
     * Inner class for status response
     */
    public static class ReportStatusResponse {
        private String reportCode;
        private ReportStatus status;
        private boolean readyForDownload;
        private String fileName;
        private String errorMessage;

        public ReportStatusResponse(String reportCode, ReportStatus status, boolean readyForDownload,
                String fileName, String errorMessage) {
            this.reportCode = reportCode;
            this.status = status;
            this.readyForDownload = readyForDownload;
            this.fileName = fileName;
            this.errorMessage = errorMessage;
        }

        public String getReportCode() {
            return reportCode;
        }

        public ReportStatus getStatus() {
            return status;
        }

        public boolean isReadyForDownload() {
            return readyForDownload;
        }

        public String getFileName() {
            return fileName;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
