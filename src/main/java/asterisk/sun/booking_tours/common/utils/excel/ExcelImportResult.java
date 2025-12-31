package asterisk.sun.booking_tours.common.utils.excel;

import java.util.List;

/**
 * Result of Excel import operation
 * Contains successfully imported items and any errors encountered
 */
public class ExcelImportResult<T> {
    private List<T> successItems;
    private List<ExcelImportError> errors;
    private int totalRows;
    private int successCount;
    private int errorCount;

    public ExcelImportResult() {}

    public ExcelImportResult(List<T> successItems, List<ExcelImportError> errors, int totalRows) {
        this.successItems = successItems;
        this.errors = errors;
        this.totalRows = totalRows;
        this.successCount = successItems != null ? successItems.size() : 0;
        this.errorCount = errors != null ? errors.size() : 0;
    }

    public List<T> getSuccessItems() {
        return successItems;
    }

    public void setSuccessItems(List<T> successItems) {
        this.successItems = successItems;
        this.successCount = successItems != null ? successItems.size() : 0;
    }

    public List<ExcelImportError> getErrors() {
        return errors;
    }

    public void setErrors(List<ExcelImportError> errors) {
        this.errors = errors;
        this.errorCount = errors != null ? errors.size() : 0;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Error detail for a specific row
     */
    public static class ExcelImportError {
        private int rowNumber;
        private String columnName;
        private String message;
        private String rawValue;

        public ExcelImportError() {}

        public ExcelImportError(int rowNumber, String columnName, String message, String rawValue) {
            this.rowNumber = rowNumber;
            this.columnName = columnName;
            this.message = message;
            this.rawValue = rawValue;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(int rowNumber) {
            this.rowNumber = rowNumber;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRawValue() {
            return rawValue;
        }

        public void setRawValue(String rawValue) {
            this.rawValue = rawValue;
        }

        @Override
        public String toString() {
            return String.format("Row %d, Column '%s': %s (value: '%s')",
                rowNumber, columnName, message, rawValue);
        }
    }
}
