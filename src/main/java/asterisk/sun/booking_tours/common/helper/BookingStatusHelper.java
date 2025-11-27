package asterisk.sun.booking_tours.common.helper;

import asterisk.sun.booking_tours.core.booking.BookingStatus;

public class BookingStatusHelper {

    /**
     * Get Bootstrap badge class for booking status
     * @param status BookingStatus enum
     * @return Bootstrap badge class (e.g., "badge-warning", "badge-success")
     */
    public static String getBadgeClass(BookingStatus status) {
        if (status == null) {
            return "badge-secondary";
        }

        switch (status) {
            case PENDING:
                return "badge-warning";
            case CONFIRMED:
                return "badge-info";
            case PAID:
                return "badge-success";
            case COMPLETED:
                return "badge-primary";
            case CANCELLED:
                return "badge-danger";
            case REFUNDED:
                return "badge-secondary";
            default:
                return "badge-secondary";
        }
    }

    /**
     * Get display name for booking status
     * @param status BookingStatus enum
     * @return Display name
     */
    public static String getDisplayName(BookingStatus status) {
        if (status == null) {
            return "UNKNOWN";
        }
        return status.name();
    }

    /**
     * Get full badge HTML class string
     * @param status BookingStatus enum
     * @return Full class string (e.g., "badge badge-warning")
     */
    public static String getFullBadgeClass(BookingStatus status) {
        return "badge " + getBadgeClass(status);
    }
}
