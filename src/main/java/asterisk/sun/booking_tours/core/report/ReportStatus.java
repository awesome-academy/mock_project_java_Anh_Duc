package asterisk.sun.booking_tours.core.report;

public enum ReportStatus {
    PENDING("Pending", "fas fa-clock", "badge-warning"),
    PROCESSING("Processing", "fas fa-spinner", "badge-info"),
    COMPLETED("Completed", "fas fa-check-circle", "badge-success"),
    FAILED("Failed", "fas fa-times-circle", "badge-danger"),
    CANCELLED("Cancelled", "fas fa-ban", "badge-secondary");

    private final String label;
    private final String icon;
    private final String badgeClass;

    ReportStatus(String label, String icon, String badgeClass) {
        this.label = label;
        this.icon = icon;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
