package asterisk.sun.booking_tours.application.api.booking;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.booking.payload.RequestCancelBookingDTO;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDepartures;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientCancelBookingService {
    private final BookingRepository bookingRepository;
    private final TourDeparturesRepository tourDeparturesRepository;

    public ClientCancelBookingService(BookingRepository bookingRepository, TourDeparturesRepository tourDeparturesRepository) {
        this.bookingRepository = bookingRepository;
        this.tourDeparturesRepository = tourDeparturesRepository;
    }

    public void cancel(RequestCancelBookingDTO requestCancelBookingDTO) {
        Booking booking = bookingRepository.findById(requestCancelBookingDTO.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking with the given ID does not exist."));

        // Implement cancellation logic here
        booking.setStatus(BookingStatus.CANCELLED);

        booking.setCancellationReason(requestCancelBookingDTO.getReason());

        updateAvailableSlots(booking);

        bookingRepository.save(booking);
    }

    private void updateAvailableSlots(Booking booking) {
        TourDepartures tourDeparture = booking.getTourDeparture();
        int totalCancelledSlots = booking.getNumAdults() + booking.getNumChild();
        tourDeparture.incrementAvailableSlots(totalCancelledSlots);
        tourDeparturesRepository.save(tourDeparture);
    }
}
