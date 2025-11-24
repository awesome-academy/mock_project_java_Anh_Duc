package asterisk.sun.booking_tours.core.user;

public enum UserStatus {
    ACTIVE("Active", "Account is active and in good standing"),
    INACTIVE("Inactive", "Account is not activated"),
    PENDING("Pending", "Account is awaiting verification");

    private final String displayName;
    private final String description;

    UserStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canLogin() {
        return this == ACTIVE || this == INACTIVE;
    }

    public boolean isBlocked() {
        return this == PENDING;
    }
}
