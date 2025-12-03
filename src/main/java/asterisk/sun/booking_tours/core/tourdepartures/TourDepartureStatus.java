package asterisk.sun.booking_tours.core.tourdepartures;

public enum TourDepartureStatus {
    SCHEDULED(
            "Scheduled",
            "fas fa-calendar-alt",
            "badge-primary"),
    CONFIRMED(
            "Confirmed",
            "fas fa-check-circle",
            "badge-success"),
    CANCELLED(
            "Cancelled",
            "fas fa-times-circle",
            "badge-danger"),
    COMPLETED(
            "Completed",
            "fas fa-check",
            "badge-secondary"),
    FULL(
            "Full",
            "fas fa-exclamation-triangle",
            "badge-warning");

    private final String label;
    private final String icon;
    private final String badgeClass;

    TourDepartureStatus(String label, String icon, String badgeClass) {
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
