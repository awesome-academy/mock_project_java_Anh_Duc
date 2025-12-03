package asterisk.sun.booking_tours.core.payment;

public enum PaymentStatus {
    PENDING("Pending", "fas fa-clock", "badge-warning"),
    PROCESSING("Processing", "fas fa-spinner", "badge-info"),
    COMPLETED("Completed", "fas fa-check-circle", "badge-success"),
    FAILED("Failed", "fas fa-times-circle", "badge-danger"),
    CANCELLED("Cancelled", "fas fa-ban", "badge-dark"),
    REFUNDED("Refunded", "fas fa-undo", "badge-secondary");

    private final String label;
    private final String icon;
    private final String badgeClass;

    PaymentStatus(String label, String icon, String badgeClass) {
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
