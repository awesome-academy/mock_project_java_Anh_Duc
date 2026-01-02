package asterisk.sun.booking_tours.common.utils.excel;

/**
 * Exception thrown when Excel export fails
 */
public class ExcelExportException extends RuntimeException {

    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
