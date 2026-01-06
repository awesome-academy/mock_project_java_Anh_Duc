package asterisk.sun.booking_tours.application.api.booking.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for batch booking result.
 * Contains information about successful and failed bookings.
 */
public class BatchBookingResultDTO {

    private int totalRequested;
    private int successCount;
    private int failedCount;
    private List<BookingResult> results;

    public BatchBookingResultDTO() {
        this.results = new ArrayList<>();
    }

    public int getTotalRequested() {
        return totalRequested;
    }

    public void setTotalRequested(int totalRequested) {
        this.totalRequested = totalRequested;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public List<BookingResult> getResults() {
        return results;
    }

    public void setResults(List<BookingResult> results) {
        this.results = results;
    }

    public void addResult(BookingResult result) {
        this.results.add(result);
    }

    /**
     * Inner class representing individual booking result
     */
    public static class BookingResult {
        private Long tourDepartureId;
        private String tourName;
        private String bookingCode;
        private boolean success;
        private String message;

        public BookingResult() {
        }

        public BookingResult(Long tourDepartureId, String tourName, String bookingCode, boolean success, String message) {
            this.tourDepartureId = tourDepartureId;
            this.tourName = tourName;
            this.bookingCode = bookingCode;
            this.success = success;
            this.message = message;
        }

        public Long getTourDepartureId() {
            return tourDepartureId;
        }

        public void setTourDepartureId(Long tourDepartureId) {
            this.tourDepartureId = tourDepartureId;
        }

        public String getTourName() {
            return tourName;
        }

        public void setTourName(String tourName) {
            this.tourName = tourName;
        }

        public String getBookingCode() {
            return bookingCode;
        }

        public void setBookingCode(String bookingCode) {
            this.bookingCode = bookingCode;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
