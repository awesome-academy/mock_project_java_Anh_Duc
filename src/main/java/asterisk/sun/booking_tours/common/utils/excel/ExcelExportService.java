package asterisk.sun.booking_tours.common.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generic Excel Export Service using Apache POI and Reflection
 * Supports mapping DTO fields to Excel columns using @ExcelColumn annotation
 */
@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Export list of DTOs to Excel file bytes
     *
     * @param data      List of DTOs to export
     * @param dtoClass  DTO class with @ExcelColumn annotations
     * @param sheetName Name of the Excel sheet
     * @param <T>       DTO type
     * @return byte array of Excel file
     */
    public <T> byte[] exportToExcel(List<T> data, Class<T> dtoClass, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Get field mappings sorted by index
            List<FieldMapping> fieldMappings = buildFieldMappings(dtoClass);

            // Create header row with style
            CellStyle headerStyle = createHeaderStyle(workbook);
            createHeaderRow(sheet, fieldMappings, headerStyle);

            // Create data rows
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            createDataRows(sheet, data, fieldMappings, dataStyle, dateStyle);

            // Auto-size columns
            for (int i = 0; i < fieldMappings.size(); i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
                // Set maximum width
                if (sheet.getColumnWidth(i) > 15000) {
                    sheet.setColumnWidth(i, 15000);
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new ExcelExportException("Failed to export Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Build field mappings using Reflection, sorted by column index
     */
    private <T> List<FieldMapping> buildFieldMappings(Class<T> dtoClass) {
        List<FieldMapping> mappings = new ArrayList<>();
        int autoIndex = 0;

        for (Field field : getAllFields(dtoClass)) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation == null || !annotation.exportable()) {
                continue;
            }

            field.setAccessible(true);
            int columnIndex = annotation.index() >= 0 ? annotation.index() : autoIndex;
            mappings.add(new FieldMapping(field, annotation, columnIndex));
            autoIndex++;
        }

        // Sort by column index
        mappings.sort(Comparator.comparingInt(FieldMapping::columnIndex));

        return mappings;
    }

    /**
     * Get all fields including inherited fields
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Create header row
     */
    private void createHeaderRow(Sheet sheet, List<FieldMapping> fieldMappings, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        int columnIndex = 0;

        for (FieldMapping mapping : fieldMappings) {
            Cell cell = headerRow.createCell(columnIndex++);
            cell.setCellValue(mapping.annotation().value());
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Create data rows
     */
    private <T> void createDataRows(Sheet sheet, List<T> data, List<FieldMapping> fieldMappings,
            CellStyle dataStyle, CellStyle dateStyle) {
        int rowIndex = 1;

        for (T item : data) {
            Row row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            for (FieldMapping mapping : fieldMappings) {
                Cell cell = row.createCell(columnIndex++);
                Object value = getFieldValue(item, mapping.field());
                setCellValue(cell, value, mapping.field().getType(), dataStyle, dateStyle);
            }
        }
    }

    /**
     * Get field value using reflection
     */
    private Object getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Set cell value based on type
     */
    private void setCellValue(Cell cell, Object value, Class<?> type, CellStyle dataStyle, CellStyle dateStyle) {
        if (value == null) {
            cell.setCellValue("");
            cell.setCellStyle(dataStyle);
            return;
        }

        if (type == String.class) {
            cell.setCellValue(value.toString());
            cell.setCellStyle(dataStyle);
        } else if (type == Integer.class || type == int.class) {
            cell.setCellValue(((Number) value).intValue());
            cell.setCellStyle(dataStyle);
        } else if (type == Long.class || type == long.class) {
            cell.setCellValue(((Number) value).longValue());
            cell.setCellStyle(dataStyle);
        } else if (type == Double.class || type == double.class) {
            cell.setCellValue(((Number) value).doubleValue());
            cell.setCellStyle(dataStyle);
        } else if (type == Float.class || type == float.class) {
            cell.setCellValue(((Number) value).floatValue());
            cell.setCellStyle(dataStyle);
        } else if (type == Boolean.class || type == boolean.class) {
            cell.setCellValue((Boolean) value);
            cell.setCellStyle(dataStyle);
        } else if (type == LocalDate.class) {
            cell.setCellValue(((LocalDate) value).format(DATE_FORMATTER));
            cell.setCellStyle(dateStyle);
        } else if (type == LocalDateTime.class) {
            cell.setCellValue(((LocalDateTime) value).format(DATE_TIME_FORMATTER));
            cell.setCellStyle(dateStyle);
        } else if (type.isEnum()) {
            cell.setCellValue(value.toString());
            cell.setCellStyle(dataStyle);
        } else {
            cell.setCellValue(value.toString());
            cell.setCellStyle(dataStyle);
        }
    }

    /**
     * Create header cell style
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Background color
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Border
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Font
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        // Alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * Create data cell style
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Border
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Alignment
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        return style;
    }

    /**
     * Create date cell style
     */
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        // Border
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    /**
     * Field mapping record
     */
    private record FieldMapping(Field field, ExcelColumn annotation, int columnIndex) {
    }
}
