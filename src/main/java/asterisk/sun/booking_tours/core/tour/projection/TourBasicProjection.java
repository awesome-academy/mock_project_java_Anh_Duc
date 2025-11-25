package asterisk.sun.booking_tours.core.tour.projection;

public interface TourBasicProjection {
    Long getId();
    String getName();
    String getTitle();
    String getDescription();
    String getSlug();
    Double getPrice();
    String getThumbnailUrl();
    String getDepartureLocation();
    String getMainDestination();
    Integer getDurationDays();
    Integer getDurationNights();
    Double getPriceAdult();
    Double getPriceChild();
    String getCurrency();
}
