package asterisk.sun.booking_tours.core.coupon;

public enum CouponStatus {
    ACTIVE("Active", "fas fa-check-circle", "badge-success"),
    INACTIVE("Inactive", "fas fa-ban", "badge-secondary"),
    EXPIRED("Expired", "fas fa-hourglass-end", "badge-danger");

    private final String label;
    private final String icon;
    private final String badgeClass;

    CouponStatus(String label, String icon, String badgeClass) {
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
