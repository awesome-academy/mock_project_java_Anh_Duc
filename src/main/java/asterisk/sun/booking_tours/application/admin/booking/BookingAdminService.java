package asterisk.sun.booking_tours.application.admin.booking;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.booking.dto.DetailBookingDTO;
import asterisk.sun.booking_tours.application.admin.booking.dto.FormCreateBookingDTO;
import asterisk.sun.booking_tours.application.admin.booking.dto.FormEditBookingDTO;
import asterisk.sun.booking_tours.application.admin.booking.dto.ListBookingDTO;
import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.DashboardService;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TopTourStatisticRequestDTO;
import asterisk.sun.booking_tours.application.rest.admin.dashboard.dto.TourStatisticDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class BookingAdminService extends BaseServiceController<BookingRepository> {

    private static final Logger logger = LoggerFactory.getLogger(BookingAdminService.class);

    private final UserRepository userRepository;
    private final TourDeparturesRepository tourDeparturesRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final DashboardService dashboardService;

    public BookingAdminService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            TourDeparturesRepository tourDeparturesRepository,
            SimpMessagingTemplate messagingTemplate,
            DashboardService dashboardService) {
        super(bookingRepository);
        this.userRepository = userRepository;
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.messagingTemplate = messagingTemplate;
        this.dashboardService = dashboardService;
    }

    /**
     * Query bookings by keyword
     */
    public List<ListBookingDTO> queryBookingsByKeyword(String keyword) {
        List<Booking> bookings = repository.searchByKeyword(keyword);

        return bookings.stream()
                .map(booking -> {
                    ListBookingDTO dto = MapperHelper.map(booking, ListBookingDTO.class);

                    if (booking.getUser() != null) {
                        dto.setUserId(booking.getUser().getId());
                        dto.setUsername(booking.getUser().getUsername());
                    }

                    if (booking.getTourDeparture() != null) {
                        dto.setTourDepartureId(booking.getTourDeparture().getId());
                        if (booking.getTourDeparture().getTour() != null) {
                            dto.setTourName(booking.getTourDeparture().getTour().getName());
                        }
                    }

                    return dto;
                })
                .toList();
    }

    /**
     * Query bookings by status
     */
    public List<ListBookingDTO> queryBookingsByStatus(BookingStatus status) {
        List<Booking> bookings = repository.findByStatus(status);

        return bookings.stream()
                .map(booking -> {
                    ListBookingDTO dto = MapperHelper.map(booking, ListBookingDTO.class);

                    // Set status explicitly
                    dto.setStatus(booking.getStatus());

                    if (booking.getUser() != null) {
                        dto.setUserId(booking.getUser().getId());
                        dto.setUsername(booking.getUser().getUsername());
                    }

                    if (booking.getTourDeparture() != null) {
                        dto.setTourDepartureId(booking.getTourDeparture().getId());
                        if (booking.getTourDeparture().getTour() != null) {
                            dto.setTourName(booking.getTourDeparture().getTour().getName());
                        }
                    }

                    return dto;
                })
                .toList();
    }

    /**
     * Create a new booking
     */
    public void createBooking(FormCreateBookingDTO formCreateBookingDTO) {
        Booking booking = new Booking();

        // Generate unique booking code
        booking.setCode(generateBookingCode());

        // Set basic fields
        booking.setStatus(formCreateBookingDTO.getStatus());
        booking.setNotes(formCreateBookingDTO.getNotes());
        booking.setNumAdults(formCreateBookingDTO.getNumAdults());
        booking.setNumChild(formCreateBookingDTO.getNumChild());
        booking.setSubTotal(formCreateBookingDTO.getSubTotal());
        booking.setDiscount(formCreateBookingDTO.getDiscount());
        booking.setFinalTotal(formCreateBookingDTO.getFinalTotal());
        booking.setContactName(formCreateBookingDTO.getContactName());
        booking.setContactPhone(formCreateBookingDTO.getContactPhone());
        booking.setContactEmail(formCreateBookingDTO.getContactEmail());

        // Set user
        User user = userRepository.findById(formCreateBookingDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with id: " + formCreateBookingDTO.getUserId()));
        booking.setUser(user);

        // Set tour departure
        TourDeparture tourDeparture = tourDeparturesRepository.findById(formCreateBookingDTO.getTourDepartureId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tour Departure not found with id: " + formCreateBookingDTO.getTourDepartureId()));
        booking.setTourDeparture(tourDeparture);

        repository.save(booking);
    }

    /**
     * Get booking by ID for editing
     */
    public FormEditBookingDTO getBookingById(Long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        FormEditBookingDTO dto = MapperHelper.map(booking, FormEditBookingDTO.class);

        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
        }

        if (booking.getTourDeparture() != null) {
            dto.setTourDepartureId(booking.getTourDeparture().getId());
        }

        return dto;
    }

    /**
     * Get booking details by ID
     */
    public DetailBookingDTO getBookingDetailById(Long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        // Use ModelMapper for basic fields (STRICT strategy avoids ambiguity)
        DetailBookingDTO dto = MapperHelper.map(booking, DetailBookingDTO.class);

        // Manual mapping for nested properties to avoid ambiguity
        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
            dto.setUsername(booking.getUser().getUsername());
        }

        if (booking.getTourDeparture() != null) {
            dto.setTourDepartureId(booking.getTourDeparture().getId());
            dto.setDepartureDate(booking.getTourDeparture().getDepartureDate());
            if (booking.getTourDeparture().getTour() != null) {
                dto.setTourName(booking.getTourDeparture().getTour().getName());
            }
        }

        return dto;
    }

    /**
     * Update an existing booking
     */
    public void updateBooking(Long id, FormEditBookingDTO formEditBookingDTO) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        // Update basic fields
        booking.setStatus(formEditBookingDTO.getStatus());
        booking.setNotes(formEditBookingDTO.getNotes());
        booking.setNumAdults(formEditBookingDTO.getNumAdults());
        booking.setNumChild(formEditBookingDTO.getNumChild());
        booking.setSubTotal(formEditBookingDTO.getSubTotal());
        booking.setDiscount(formEditBookingDTO.getDiscount());
        booking.setFinalTotal(formEditBookingDTO.getFinalTotal());
        booking.setContactName(formEditBookingDTO.getContactName());
        booking.setContactPhone(formEditBookingDTO.getContactPhone());
        booking.setContactEmail(formEditBookingDTO.getContactEmail());

        // Update user if changed
        if (!booking.getUser().getId().equals(formEditBookingDTO.getUserId())) {
            User user = userRepository.findById(formEditBookingDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User not found with id: " + formEditBookingDTO.getUserId()));
            booking.setUser(user);
        }

        // Update tour departure if changed
        if (!booking.getTourDeparture().getId().equals(formEditBookingDTO.getTourDepartureId())) {
            TourDeparture tourDeparture = tourDeparturesRepository.findById(formEditBookingDTO.getTourDepartureId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Tour Departure not found with id: " + formEditBookingDTO.getTourDepartureId()));
            booking.setTourDeparture(tourDeparture);
        }

        if (booking.getStatus() == BookingStatus.PAID || booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.PENDING) {
            pushTopToursUpdate();
        }

        repository.save(booking);
    }

    /**
     * Update booking status
     */
    public void updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));

        booking.setStatus(status);
        repository.save(booking);

        if (status == BookingStatus.PAID || status == BookingStatus.COMPLETED || status == BookingStatus.PENDING) {
            pushTopToursUpdate();
        }
    }

    /**
     * Push top tours statistics update via WebSocket
     */
    private void pushTopToursUpdate() {
        try {
            TopTourStatisticRequestDTO request = new TopTourStatisticRequestDTO();
            request.setLimit(10);
            List<TourStatisticDTO> topTours = dashboardService.getTopPopularTours(request);
            messagingTemplate.convertAndSend("/topic/dashboard/top-tours", topTours);
            logger.info("Pushed top tours update via WebSocket");
        } catch (Exception e) {
            logger.error("Failed to push top tours update: {}", e.getMessage());
        }
    }

    /**
     * Delete a booking
     */
    public void deleteBooking(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Booking not found with id: " + id);
        }
        repository.deleteById(id);
    }

    /**
     * Generate unique booking code
     */
    private String generateBookingCode() {
        String code;
        do {
            code = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (repository.existsByCode(code));
        return code;
    }
}
