package asterisk.sun.booking_tours.application.rest.admin.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.rest.admin.report.dto.CreateReportRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportRequestMessage;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.ReportResponseDTO;
import asterisk.sun.booking_tours.application.rest.admin.report.dto.RevenueDataDTO;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.payment.Payment;
import asterisk.sun.booking_tours.core.payment.PaymentRepository;
import asterisk.sun.booking_tours.core.payment.PaymentStatus;
import asterisk.sun.booking_tours.core.report.ReportStatus;
import asterisk.sun.booking_tours.core.report.ReportType;
import asterisk.sun.booking_tours.core.report.RevenueReport;
import asterisk.sun.booking_tours.core.report.RevenueReportRepository;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;

@Service
public class ReportService {

    @Autowired
    private RevenueReportRepository reportRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExcelReportGenerator excelReportGenerator;

    @Autowired
    private ReportQueueProducer reportQueueProducer;

    /**
     * Create a new report request and queue it for processing
     */
    @Transactional
    public ReportResponseDTO createReportRequest(CreateReportRequestDTO request, Long userId) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate unique report code
        String reportCode = generateReportCode();

        // Create report entity
        RevenueReport report = new RevenueReport(
                reportCode,
                request.getReportType(),
                request.getStartDate(),
                request.getEndDate(),
                user
        );
        report = reportRepository.save(report);

        // Create message and send to queue
        ReportRequestMessage message = new ReportRequestMessage(
                report.getId(),
                reportCode,
                request.getReportType(),
                request.getStartDate(),
                request.getEndDate(),
                userId
        );
        reportQueueProducer.sendReportRequest(message);

        return mapToDTO(report);
    }

    /**
     * Get report by ID
     */
    public ReportResponseDTO getReportById(Long reportId) {
        RevenueReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToDTO(report);
    }

    /**
     * Get report by code
     */
    public ReportResponseDTO getReportByCode(String reportCode) {
        RevenueReport report = reportRepository.findByReportCode(reportCode)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToDTO(report);
    }

    /**
     * Get all reports with pagination
     */
    public Page<RevenueReport> getAllReports(ReportRequestDTO request) {
        return reportRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(request.getPage(), request.getLimit()));
    }

    /**
     * Get report entity for download
     */
    public RevenueReport getReportForDownload(String reportCode) {
        return reportRepository.findByReportCode(reportCode)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    /**
     * Generate report file as byte array for download (on-the-fly generation)
     */
    public byte[] generateReportForDownload(String reportCode) throws IOException {
        RevenueReport report = reportRepository.findByReportCode(reportCode)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() != ReportStatus.COMPLETED) {
            throw new RuntimeException("Report is not ready for download. Current status: " + report.getStatus());
        }

        // Fetch revenue data
        List<RevenueDataDTO> revenueData = fetchRevenueData(
                report.getStartDate(),
                report.getEndDate(),
                report.getReportType()
        );

        // Generate Excel file as byte array
        return excelReportGenerator.generateRevenueReportAsBytes(
                report.getReportCode(),
                report.getReportType(),
                report.getStartDate(),
                report.getEndDate(),
                revenueData
        );
    }

    /**
     * Generate filename for report download
     */
    public String getReportFileName(String reportCode) {
        RevenueReport report = reportRepository.findByReportCode(reportCode)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return excelReportGenerator.generateFileName(reportCode, report.getReportType());
    }

    /**
     * Process report generation (called by queue consumer)
     * This method validates the report request and marks it as completed.
     * The actual Excel file is generated on-the-fly when user downloads.
     */
    @Transactional
    public void processReportGeneration(ReportRequestMessage message) {
        RevenueReport report = reportRepository.findById(message.getReportId())
                .orElseThrow(() -> new RuntimeException("Report not found: " + message.getReportId()));

        try {
            // Update status to processing
            report.setStatus(ReportStatus.PROCESSING);
            reportRepository.save(report);

            // Validate that we can fetch revenue data (pre-validation)
            List<RevenueDataDTO> revenueData = fetchRevenueData(
                    message.getStartDate(),
                    message.getEndDate(),
                    message.getReportType()
            );

            // Generate filename for reference
            String fileName = excelReportGenerator.generateFileName(
                    message.getReportCode(),
                    message.getReportType()
            );

            // Update report metadata (no file stored on server)
            report.setFileName(fileName);
            report.setGeneratedAt(LocalDateTime.now());
            report.setStatus(ReportStatus.COMPLETED);
            reportRepository.save(report);

        } catch (Exception e) {
            // Update status to failed
            report.setStatus(ReportStatus.FAILED);
            report.setErrorMessage(e.getMessage());
            reportRepository.save(report);
            throw new RuntimeException("Failed to process report: " + e.getMessage(), e);
        }
    }

    /**
     * Fetch revenue data based on report type and date range
     */
    private List<RevenueDataDTO> fetchRevenueData(LocalDate startDate, LocalDate endDate, ReportType reportType) {
        List<RevenueDataDTO> result = new ArrayList<>();

        // Fetch all completed payments within date range
        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.COMPLETED);

        // Filter by date range
        payments = payments.stream()
                .filter(p -> p.getCreatedAt() != null)
                .filter(p -> {
                    LocalDate paymentDate = p.getCreatedAt().toLocalDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Fetch all bookings
        List<Booking> allBookings = bookingRepository.findAll();

        // Filter bookings by date range
        List<Booking> bookingsInRange = allBookings.stream()
                .filter(b -> b.getCreatedAt() != null)
                .filter(b -> {
                    LocalDate bookingDate = b.getCreatedAt().toLocalDate();
                    return !bookingDate.isBefore(startDate) && !bookingDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        switch (reportType) {
            case REVENUE_DAILY:
                result = generateDailyReport(startDate, endDate, payments, bookingsInRange);
                break;
            case REVENUE_MONTHLY:
                result = generateMonthlyReport(startDate, endDate, payments, bookingsInRange);
                break;
            case REVENUE_YEARLY:
                result = generateYearlyReport(startDate, endDate, payments, bookingsInRange);
                break;
            case TOUR_PERFORMANCE:
                result = generateTourPerformanceReport(payments, bookingsInRange);
                break;
            default:
                result = generateDailyReport(startDate, endDate, payments, bookingsInRange);
        }

        return result;
    }

    private List<RevenueDataDTO> generateDailyReport(LocalDate startDate, LocalDate endDate,
                                                      List<Payment> payments, List<Booking> bookings) {
        List<RevenueDataDTO> result = new ArrayList<>();

        // Group payments by date
        Map<LocalDate, List<Payment>> paymentsByDate = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getCreatedAt().toLocalDate()));

        // Group bookings by date
        Map<LocalDate, List<Booking>> bookingsByDate = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getCreatedAt().toLocalDate()));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            List<Payment> dayPayments = paymentsByDate.getOrDefault(currentDate, new ArrayList<>());
            List<Booking> dayBookings = bookingsByDate.getOrDefault(currentDate, new ArrayList<>());

            BigDecimal totalRevenue = dayPayments.stream()
                    .map(Payment::getAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalBookings = dayBookings.size();
            long completedBookings = dayBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.PAID)
                    .count();
            long cancelledBookings = dayBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .count();

            RevenueDataDTO dto = new RevenueDataDTO(
                    currentDate,
                    totalBookings,
                    completedBookings,
                    cancelledBookings,
                    totalRevenue
            );
            result.add(dto);

            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    private List<RevenueDataDTO> generateMonthlyReport(LocalDate startDate, LocalDate endDate,
                                                        List<Payment> payments, List<Booking> bookings) {
        List<RevenueDataDTO> result = new ArrayList<>();

        // Group by month
        Map<String, List<Payment>> paymentsByMonth = payments.stream()
                .collect(Collectors.groupingBy(p ->
                        p.getCreatedAt().getYear() + "-" + String.format("%02d", p.getCreatedAt().getMonthValue())));

        Map<String, List<Booking>> bookingsByMonth = bookings.stream()
                .collect(Collectors.groupingBy(b ->
                        b.getCreatedAt().getYear() + "-" + String.format("%02d", b.getCreatedAt().getMonthValue())));

        LocalDate currentMonth = startDate.withDayOfMonth(1);
        while (!currentMonth.isAfter(endDate)) {
            String monthKey = currentMonth.getYear() + "-" + String.format("%02d", currentMonth.getMonthValue());

            List<Payment> monthPayments = paymentsByMonth.getOrDefault(monthKey, new ArrayList<>());
            List<Booking> monthBookings = bookingsByMonth.getOrDefault(monthKey, new ArrayList<>());

            BigDecimal totalRevenue = monthPayments.stream()
                    .map(Payment::getAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalBookings = monthBookings.size();
            long completedBookings = monthBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.PAID)
                    .count();
            long cancelledBookings = monthBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .count();

            RevenueDataDTO dto = new RevenueDataDTO(
                    currentMonth,
                    totalBookings,
                    completedBookings,
                    cancelledBookings,
                    totalRevenue
            );
            result.add(dto);

            currentMonth = currentMonth.plusMonths(1);
        }

        return result;
    }

    private List<RevenueDataDTO> generateYearlyReport(LocalDate startDate, LocalDate endDate,
                                                       List<Payment> payments, List<Booking> bookings) {
        List<RevenueDataDTO> result = new ArrayList<>();

        // Group by year
        Map<Integer, List<Payment>> paymentsByYear = payments.stream()
                .collect(Collectors.groupingBy(p -> p.getCreatedAt().getYear()));

        Map<Integer, List<Booking>> bookingsByYear = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getCreatedAt().getYear()));

        int currentYear = startDate.getYear();
        while (currentYear <= endDate.getYear()) {
            List<Payment> yearPayments = paymentsByYear.getOrDefault(currentYear, new ArrayList<>());
            List<Booking> yearBookings = bookingsByYear.getOrDefault(currentYear, new ArrayList<>());

            BigDecimal totalRevenue = yearPayments.stream()
                    .map(Payment::getAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalBookings = yearBookings.size();
            long completedBookings = yearBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.PAID)
                    .count();
            long cancelledBookings = yearBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .count();

            RevenueDataDTO dto = new RevenueDataDTO(
                    LocalDate.of(currentYear, 1, 1),
                    totalBookings,
                    completedBookings,
                    cancelledBookings,
                    totalRevenue
            );
            result.add(dto);

            currentYear++;
        }

        return result;
    }

    private List<RevenueDataDTO> generateTourPerformanceReport(List<Payment> payments, List<Booking> bookings) {
        List<RevenueDataDTO> result = new ArrayList<>();

        // Group bookings by tour
        Map<String, List<Booking>> bookingsByTour = bookings.stream()
                .filter(b -> b.getTourDeparture() != null && b.getTourDeparture().getTour() != null)
                .collect(Collectors.groupingBy(b -> b.getTourDeparture().getTour().getName()));

        for (Map.Entry<String, List<Booking>> entry : bookingsByTour.entrySet()) {
            String tourName = entry.getKey();
            List<Booking> tourBookings = entry.getValue();

            // Calculate revenue from payments related to these bookings
            BigDecimal totalRevenue = tourBookings.stream()
                    .map(Booking::getFinalTotal)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalBookings = tourBookings.size();
            long completedBookings = tourBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.PAID)
                    .count();
            long cancelledBookings = tourBookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .count();

            RevenueDataDTO dto = new RevenueDataDTO();
            dto.setTourName(tourName);
            dto.setTotalBookings(totalBookings);
            dto.setCompletedBookings(completedBookings);
            dto.setCancelledBookings(cancelledBookings);
            dto.setTotalRevenue(totalRevenue);
            if (completedBookings > 0) {
                dto.setAverageBookingValue(totalRevenue.divide(BigDecimal.valueOf(completedBookings), 2, java.math.RoundingMode.HALF_UP));
            }
            result.add(dto);
        }

        return result;
    }

    private String generateReportCode() {
        return "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ReportResponseDTO mapToDTO(RevenueReport report) {
        return ReportResponseDTO.builder()
                .id(report.getId())
                .reportCode(report.getReportCode())
                .reportType(report.getReportType())
                .status(report.getStatus())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .fileName(report.getFileName())
                .fileSize(report.getFileSize())
                .errorMessage(report.getErrorMessage())
                .generatedAt(report.getGeneratedAt())
                .createdAt(report.getCreatedAt())
                .requestedByUsername(report.getRequestedBy() != null ? report.getRequestedBy().getUsername() : null)
                .build();
    }
}
