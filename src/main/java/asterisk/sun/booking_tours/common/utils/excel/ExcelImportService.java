package asterisk.sun.booking_tours.common.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Generic Excel Import Service using Apache POI and Reflection
 * Supports mapping Excel columns to DTO fields using @ExcelColumn annotation
 */
@Service
public class ExcelImportService {

    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";

    /**
     * Import data from Excel file to list of DTOs
     *
     * @param file     Uploaded Excel file
     * @param dtoClass Target DTO class with @ExcelColumn annotations
     * @param <T>      DTO type
     * @return Import result containing success items and errors
     */
    public <T> ExcelImportResult<T> importFromExcel(MultipartFile file, Class<T> dtoClass) {
        validateFile(file);

        List<T> successItems = new ArrayList<>();
        List<ExcelImportResult.ExcelImportError> errors = new ArrayList<>();
        int totalRows = 0;

        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new ExcelImportException("Excel file is empty");
            }

            // Get header row and build column index map
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ExcelImportException("Header row is missing");
            }

            Map<String, Integer> headerMap = buildHeaderMap(headerRow);
            Map<Field, ColumnMapping> fieldMappings = buildFieldMappings(dtoClass, headerMap);

            // Process data rows (skip header)
            totalRows = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                try {
                    T dto = parseRow(row, dtoClass, fieldMappings, rowIndex + 1);
                    successItems.add(dto);
                } catch (RowParseException e) {
                    errors.addAll(e.getErrors());
                }
            }

        } catch (IOException e) {
            throw new ExcelImportException("Failed to read Excel file: " + e.getMessage(), e);
        }

        return new ExcelImportResult<>(successItems, errors, totalRows);
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ExcelImportException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals(XLSX_CONTENT_TYPE) && !contentType.equals(XLS_CONTENT_TYPE))) {
            throw new ExcelImportException("Invalid file type. Only Excel files (.xlsx, .xls) are supported");
        }
    }

    /**
     * Build map of header names to column indices
     */
    private Map<String, Integer> buildHeaderMap(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerName = getCellStringValue(cell).toLowerCase().trim();
                if (!headerName.isEmpty()) {
                    headerMap.put(headerName, i);
                }
            }
        }
        return headerMap;
    }

    /**
     * Build field mappings using Reflection
     */
    private <T> Map<Field, ColumnMapping> buildFieldMappings(Class<T> dtoClass, Map<String, Integer> headerMap) {
        Map<Field, ColumnMapping> mappings = new HashMap<>();

        for (Field field : dtoClass.getDeclaredFields()) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation == null) {
                continue;
            }

            field.setAccessible(true);
            int columnIndex;

            if (annotation.index() >= 0) {
                // Use explicit index
                columnIndex = annotation.index();
            } else {
                // Auto-detect by header name
                String columnName = annotation.value().toLowerCase().trim();
                Integer index = headerMap.get(columnName);
                if (index == null) {
                    if (annotation.required()) {
                        throw new ExcelImportException("Required column not found: " + annotation.value());
                    }
                    continue;
                }
                columnIndex = index;
            }

            mappings.put(field, new ColumnMapping(columnIndex, annotation));
        }

        return mappings;
    }

    /**
     * Parse a single row to DTO
     */
    private <T> T parseRow(Row row, Class<T> dtoClass, Map<Field, ColumnMapping> fieldMappings, int rowNumber)
            throws RowParseException {
        List<ExcelImportResult.ExcelImportError> errors = new ArrayList<>();
        T dto;

        try {
            dto = dtoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ExcelImportException("Cannot instantiate DTO class: " + dtoClass.getName(), e);
        }

        for (Map.Entry<Field, ColumnMapping> entry : fieldMappings.entrySet()) {
            Field field = entry.getKey();
            ColumnMapping mapping = entry.getValue();
            ExcelColumn annotation = mapping.annotation();

            Cell cell = row.getCell(mapping.columnIndex());
            String rawValue = cell != null ? getCellStringValue(cell) : "";

            try {
                Object value = convertValue(rawValue, field.getType(), annotation);

                // Check required field
                if (annotation.required() && (value == null || value.toString().isEmpty())) {
                    errors.add(new ExcelImportResult.ExcelImportError(
                            rowNumber, annotation.value(), "Required field is empty", rawValue));
                    continue;
                }

                field.set(dto, value);
            } catch (IllegalAccessException e) {
                errors.add(new ExcelImportResult.ExcelImportError(
                        rowNumber, annotation.value(), "Cannot set field value", rawValue));
            } catch (ValueConversionException e) {
                errors.add(new ExcelImportResult.ExcelImportError(
                        rowNumber, annotation.value(), e.getMessage(), rawValue));
            }
        }

        if (!errors.isEmpty()) {
            throw new RowParseException(errors);
        }

        return dto;
    }

    /**
     * Convert string value to target type using Reflection
     */
    private Object convertValue(String rawValue, Class<?> targetType, ExcelColumn annotation)
            throws ValueConversionException {
        // Use default value if empty
        if (rawValue == null || rawValue.trim().isEmpty()) {
            String defaultValue = annotation.defaultValue();
            if (defaultValue.isEmpty()) {
                return getDefaultValue(targetType);
            }
            rawValue = defaultValue;
        }

        rawValue = rawValue.trim();

        try {
            if (targetType == String.class) {
                return rawValue;
            } else if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(rawValue);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(rawValue);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(rawValue);
            } else if (targetType == Float.class || targetType == float.class) {
                return Float.parseFloat(rawValue);
            } else if (targetType == BigDecimal.class) {
                return new BigDecimal(rawValue);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return parseBoolean(rawValue);
            } else if (targetType == LocalDate.class) {
                return LocalDate.parse(rawValue);
            } else if (targetType == LocalDateTime.class) {
                return LocalDateTime.parse(rawValue);
            } else if (targetType.isEnum()) {
                return parseEnum(rawValue, targetType);
            }
        } catch (Exception e) {
            throw new ValueConversionException(
                    String.format("Cannot convert '%s' to %s", rawValue, targetType.getSimpleName()));
        }

        throw new ValueConversionException("Unsupported field type: " + targetType.getName());
    }

    /**
     * Get default value for primitive types
     */
    private Object getDefaultValue(Class<?> type) {
        if (type == int.class)
            return 0;
        if (type == long.class)
            return 0L;
        if (type == double.class)
            return 0.0;
        if (type == float.class)
            return 0.0f;
        if (type == boolean.class)
            return false;
        return null;
    }

    /**
     * Parse boolean value from string
     */
    private Boolean parseBoolean(String value) {
        if (value == null)
            return null;
        String v = value.toLowerCase().trim();
        return v.equals("true") || v.equals("1") || v.equals("yes") || v.equals("có") || v.equals("co");
    }

    /**
     * Parse enum value from string using Reflection
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object parseEnum(String value, Class<?> enumClass) throws ValueConversionException {
        try {
            return Enum.valueOf((Class<Enum>) enumClass, value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Try case-insensitive match
            for (Object constant : enumClass.getEnumConstants()) {
                if (constant.toString().equalsIgnoreCase(value.trim())) {
                    return constant;
                }
            }
            throw new ValueConversionException(
                    String.format("Invalid enum value '%s'. Valid values: %s",
                            value, Arrays.toString(enumClass.getEnumConstants())));
        }
    }

    /**
     * Get string value from cell regardless of cell type
     */
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                // Format number without scientific notation
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    yield String.valueOf((long) value);
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }

    /**
     * Check if row is empty
     */
    private boolean isRowEmpty(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !getCellStringValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Column mapping record
     */
    private record ColumnMapping(int columnIndex, ExcelColumn annotation) {
    }

    /**
     * Exception for value conversion errors
     */
    private static class ValueConversionException extends Exception {
        public ValueConversionException(String message) {
            super(message);
        }
    }

    /**
     * Exception for row parsing errors
     */
    private static class RowParseException extends Exception {
        private final List<ExcelImportResult.ExcelImportError> errors;

        public RowParseException(List<ExcelImportResult.ExcelImportError> errors) {
            this.errors = errors;
        }

        public List<ExcelImportResult.ExcelImportError> getErrors() {
            return errors;
        }
    }
}
