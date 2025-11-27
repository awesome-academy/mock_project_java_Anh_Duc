package asterisk.sun.booking_tours.application.client.booking;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.client.booking.payload.RequestBookingDTO;
import asterisk.sun.booking_tours.common.utils.CodeGenerator;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tourdepartures.TourDepartures;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientBookingService {
    private final TourDeparturesRepository tourDeparturesRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ClientBookingService(TourDeparturesRepository tourDeparturesRepository, BookingRepository bookingRepository,
            UserRepository userRepository) {
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void bookTour(RequestBookingDTO requestBookingDTO) {
        // Validate tour departure
        TourDepartures tourDeparture = validateTourDeparture(requestBookingDTO.getTourDepartureId());
        Tour tour = tourDeparture.getTour();
        User user = userRepository.findById(requestBookingDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with the given ID does not exist."));
        // Implement booking logic here

        PriceCalculator priceCalculator = new PriceCalculator(requestBookingDTO.getNumAdults(),
                requestBookingDTO.getNumChild(),
                tour.getPriceAdult(),
                tour.getPriceChild(),
                new BigDecimal("5"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTourDeparture(tourDeparture);
        booking.setCode(CodeGenerator.generateBookingCode(bookingRepository::existsByCode));
        booking.setNotes(requestBookingDTO.getNotes());
        booking.setNumAdults(requestBookingDTO.getNumAdults());
        booking.setNumChild(requestBookingDTO.getNumChild());
        booking.setContactName(requestBookingDTO.getContactName());
        booking.setContactEmail(requestBookingDTO.getContactEmail());
        booking.setContactPhone(requestBookingDTO.getContactPhone());
        booking.setSubTotal(priceCalculator.getSubTotal());
        booking.setDiscount(priceCalculator.getDiscount());
        booking.setFinalTotal(priceCalculator.getFinalTotal());

        booking.setStatus(BookingStatus.PENDING);

        updateAvailableSlots(tourDeparture, requestBookingDTO.getNumAdults() + requestBookingDTO.getNumChild());

        bookingRepository.save(booking);
    }

    private void updateAvailableSlots(TourDepartures tourDeparture, int totalParticipants) {
        int updatedAvailableSlots = tourDeparture.getAvailableSlots() - totalParticipants;
        if (updatedAvailableSlots < 0) {
            throw new IllegalArgumentException("Not enough available slots for the selected tour departure.");
        }

        tourDeparture.setAvailableSlots(updatedAvailableSlots);
        tourDeparturesRepository.save(tourDeparture);
    }

    private TourDepartures validateTourDeparture(Long tourDepartureId) {
        TourDepartures tourDeparture = tourDeparturesRepository.findById(tourDepartureId)
                .orElseThrow(() -> new EntityNotFoundException("Tour Departure with the given ID does not exist."));

        if (tourDeparture.getAvailableSlots() <= 0) {
            throw new EntityNotFoundException("Tour Departure has no available slots.");
        }

        if (tourDeparture.getAvailableSlots() > tourDeparture.getTotalSlots()) {
            throw new EntityNotFoundException("Tour Departure has no available slots.");
        }

        if (java.time.LocalDate.now().isAfter(tourDeparture.getReturnDate())) {
            throw new IllegalArgumentException("Booking date must be before the tour's return date.");
        }

        return tourDeparture;
    }

}
