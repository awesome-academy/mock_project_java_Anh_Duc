package asterisk.sun.booking_tours.application.rest.admin.dashboard;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.ListBookingLatestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.ListBookingLatestRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TopTourStatisticRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TourStatisticDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.UserCountDTO;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public UserCountDTO getUserCount() {
        Long totalUsers = userRepository.count();

        Long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        Long inactiveUsers = userRepository.countByStatus(UserStatus.INACTIVE);

        return new UserCountDTO(totalUsers, activeUsers, inactiveUsers);
    }

    public List<ListBookingLatestDTO> getLatestBookingForUser(ListBookingLatestRequestDTO request) {
        List<Booking> latestBookings = bookingRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, request.getLimit()));

        return latestBookings.stream().map(booking -> ListBookingLatestDTO.builder()
                .id(String.valueOf(booking.getId()))
                .code(booking.getCode())
                .tourName(booking.getTourDeparture().getTour().getName())
                .contactName(booking.getContactName())
                .status(booking.getStatus())
                .finalTotal(booking.getFinalTotal())
                .createdAt(booking.getCreatedAt())
                .build()).toList();
    }

    /**
     * Get top popular tours statistics with booking count and revenue
     * Only considers bookings with status CONFIRMED, PAID, or COMPLETED
     */
    public List<TourStatisticDTO> getTopPopularTours(TopTourStatisticRequestDTO request) {
        List<BookingStatus> validStatuses = List.of(
                BookingStatus.CONFIRMED,
                BookingStatus.PAID,
                BookingStatus.COMPLETED
        );

        List<Object[]> results = bookingRepository.findTopPopularTourStatistics(
                validStatuses,
                PageRequest.of(0, request.getLimit())
        );

        return results.stream().map(row -> TourStatisticDTO.builder()
                .tourId(String.valueOf(row[0]))
                .tourName((String) row[1])
                .bookingCount((Long) row[2])
                .revenue((BigDecimal) row[3])
                .build()).toList();
    }
}
