package asterisk.sun.booking_tours.common.utils.excel;

/**
 * Exception thrown when Excel import fails
 */
public class ExcelImportException extends RuntimeException {

    public ExcelImportException(String message) {
        super(message);
    }

    public ExcelImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
