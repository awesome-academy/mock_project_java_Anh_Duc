package asterisk.sun.booking_tours.application.api.common.endpoint;

public class ApiV1 {
    private ApiV1() {
    }

    private static final String API_V1_PREFIX = "/api/v1";

    public static final String BOOKING_ENDPOINT = API_V1_PREFIX + "/bookings";

    public static final String TOUR_ENDPOINT = API_V1_PREFIX + "/tours";
}
