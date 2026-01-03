package asterisk.sun.booking_tours.core.report;

public enum ReportType {
    REVENUE_DAILY("Daily Revenue Report"),
    REVENUE_MONTHLY("Monthly Revenue Report"),
    REVENUE_YEARLY("Yearly Revenue Report"),
    REVENUE_CUSTOM("Custom Period Revenue Report"),
    BOOKING_SUMMARY("Booking Summary Report"),
    TOUR_PERFORMANCE("Tour Performance Report");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
