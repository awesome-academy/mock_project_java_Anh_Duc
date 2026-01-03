package asterisk.sun.booking_tours.application.rest.admin.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.application.rest.admin.report.dto.RevenueDataDTO;
import asterisk.sun.booking_tours.core.report.ReportType;

@Component
public class ExcelReportGenerator {

    @Value("${report.storage-path:./reports}")
    private String storagePath;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Generate Excel report file
     * @param reportCode Report code for file naming
     * @param reportType Type of report
     * @param startDate Start date of report period
     * @param endDate End date of report period
     * @param data List of revenue data
     * @return Path to generated file
     */
    public Path generateRevenueReport(String reportCode, ReportType reportType,
                                       LocalDate startDate, LocalDate endDate,
                                       List<RevenueDataDTO> data) throws IOException {
        // Ensure directory exists
        Path dirPath = Paths.get(storagePath);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Generate filename
        String fileName = String.format("revenue_report_%s_%s.xlsx",
                reportCode, LocalDateTime.now().format(FILE_DATE_FORMATTER));
        Path filePath = dirPath.resolve(fileName);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Revenue Report");

            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle summaryStyle = createSummaryStyle(workbook);

            int rowNum = 0;

            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportType.getDescription().toUpperCase());
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            // Period info
            Row periodRow = sheet.createRow(rowNum++);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue(String.format("Period: %s - %s",
                    startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER)));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Generated date
            Row generatedRow = sheet.createRow(rowNum++);
            Cell generatedCell = generatedRow.createCell(0);
            generatedCell.setCellValue(String.format("Generated: %s",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));

            rowNum++; // Empty row

            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Date", "Total Bookings", "Completed", "Cancelled", "Total Revenue", "Avg. Booking Value", "Tour Name"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            BigDecimal grandTotalRevenue = BigDecimal.ZERO;
            long grandTotalBookings = 0;
            long grandTotalCompleted = 0;
            long grandTotalCancelled = 0;

            for (RevenueDataDTO item : data) {
                Row dataRow = sheet.createRow(rowNum++);

                Cell dateCell = dataRow.createCell(0);
                dateCell.setCellValue(item.getDate() != null ? item.getDate().format(DATE_FORMATTER) : "N/A");
                dateCell.setCellStyle(dateStyle);

                Cell totalBookingsCell = dataRow.createCell(1);
                totalBookingsCell.setCellValue(item.getTotalBookings() != null ? item.getTotalBookings() : 0);
                totalBookingsCell.setCellStyle(dataStyle);

                Cell completedCell = dataRow.createCell(2);
                completedCell.setCellValue(item.getCompletedBookings() != null ? item.getCompletedBookings() : 0);
                completedCell.setCellStyle(dataStyle);

                Cell cancelledCell = dataRow.createCell(3);
                cancelledCell.setCellValue(item.getCancelledBookings() != null ? item.getCancelledBookings() : 0);
                cancelledCell.setCellStyle(dataStyle);

                Cell revenueCell = dataRow.createCell(4);
                revenueCell.setCellValue(item.getTotalRevenue() != null ? item.getTotalRevenue().doubleValue() : 0);
                revenueCell.setCellStyle(currencyStyle);

                Cell avgCell = dataRow.createCell(5);
                avgCell.setCellValue(item.getAverageBookingValue() != null ? item.getAverageBookingValue().doubleValue() : 0);
                avgCell.setCellStyle(currencyStyle);

                Cell tourCell = dataRow.createCell(6);
                tourCell.setCellValue(item.getTourName() != null ? item.getTourName() : "All Tours");
                tourCell.setCellStyle(dataStyle);

                // Accumulate totals
                if (item.getTotalRevenue() != null) {
                    grandTotalRevenue = grandTotalRevenue.add(item.getTotalRevenue());
                }
                if (item.getTotalBookings() != null) {
                    grandTotalBookings += item.getTotalBookings();
                }
                if (item.getCompletedBookings() != null) {
                    grandTotalCompleted += item.getCompletedBookings();
                }
                if (item.getCancelledBookings() != null) {
                    grandTotalCancelled += item.getCancelledBookings();
                }
            }

            // Summary row
            rowNum++; // Empty row before summary
            Row summaryRow = sheet.createRow(rowNum);
            Cell summaryLabelCell = summaryRow.createCell(0);
            summaryLabelCell.setCellValue("TOTAL");
            summaryLabelCell.setCellStyle(summaryStyle);

            Cell summaryTotalCell = summaryRow.createCell(1);
            summaryTotalCell.setCellValue(grandTotalBookings);
            summaryTotalCell.setCellStyle(summaryStyle);

            Cell summaryCompletedCell = summaryRow.createCell(2);
            summaryCompletedCell.setCellValue(grandTotalCompleted);
            summaryCompletedCell.setCellStyle(summaryStyle);

            Cell summaryCancelledCell = summaryRow.createCell(3);
            summaryCancelledCell.setCellValue(grandTotalCancelled);
            summaryCancelledCell.setCellStyle(summaryStyle);

            Cell summaryRevenueCell = summaryRow.createCell(4);
            summaryRevenueCell.setCellValue(grandTotalRevenue.doubleValue());
            summaryRevenueCell.setCellStyle(summaryStyle);

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                workbook.write(outputStream);
            }
        }

        return filePath;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSummaryStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    public String getStoragePath() {
        return storagePath;
    }
}
