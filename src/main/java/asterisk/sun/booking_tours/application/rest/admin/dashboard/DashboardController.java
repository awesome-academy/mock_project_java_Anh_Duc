package asterisk.sun.booking_tours.application.rest.admin.dashboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.ListBookingLatestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.ListBookingLatestRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TopTourStatisticRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TourStatisticDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.UserCountDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/admin/dashboard")
@EnableScheduling
@Tag(name = "Dashboard", description = "Dashboard API")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/users/count")
    @Operation(summary = "Get user count statistics", description = "Returns total, active, and inactive user counts")
    public ResponseEntity<UserCountDTO> getUserCount() {
        UserCountDTO userCount = dashboardService.getUserCount();
        return ResponseEntity.ok(userCount);
    }

    /**
     * Scheduled task to push user count updates via WebSocket every 5 seconds
     * This sends real-time updates to all connected clients
     */
    @Scheduled(fixedRate = 5000)
    public void pushUserCountUpdate() {
        UserCountDTO userCount = dashboardService.getUserCount();
        // Send to topic/dashboard/users - clients subscribed to this topic will receive updates
        messagingTemplate.convertAndSend("/topic/dashboard/users", userCount);
    }

    @GetMapping("/list-booking-latest")
    public ResponseEntity<List<ListBookingLatestDTO>> getLatestBooking(ListBookingLatestRequestDTO request) {
        List<ListBookingLatestDTO> latestBookings = dashboardService.getLatestBookingForUser(request);
        return ResponseEntity.ok(latestBookings);
    }

    @GetMapping("/top-tours")
    @Operation(summary = "Get top popular tours statistics", description = "Returns list of top popular tours with booking count and revenue")
    public ResponseEntity<List<TourStatisticDTO>> getTopPopularTours(TopTourStatisticRequestDTO request) {
        List<TourStatisticDTO> popularTours = dashboardService.getTopPopularTours(request);
        return ResponseEntity.ok(popularTours);
    }
}
