package asterisk.sun.booking_tours.application.api.booking;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.api.booking.dto.RequestBookingDTO;
import asterisk.sun.booking_tours.common.utils.CodeGenerator;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.coupon.Coupon;
import asterisk.sun.booking_tours.core.coupon.CouponRepository;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiBookingService {

    private static final Logger logger = LoggerFactory.getLogger(ApiBookingService.class);

    private final TourDeparturesRepository tourDeparturesRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    /**
     * Payment deadline in hours from booking creation time.
     * Configured via application.yml: booking.payment-deadline-hours (default: 24 hours)
     */
    @Value("${booking.payment-deadline-hours:24}")
    private int paymentDeadlineHours;

    public ApiBookingService(TourDeparturesRepository tourDeparturesRepository, BookingRepository bookingRepository,
            UserRepository userRepository, CouponRepository couponRepository) {
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
    }

    @Transactional
    public void bookTour(RequestBookingDTO requestBookingDTO, String email) {
        // Validate tour departure
        TourDeparture tourDeparture = validateTourDeparture(requestBookingDTO.getTourDepartureId());
        Tour tour = tourDeparture.getTour();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with the given email does not exist."));

        PriceCalculator priceCalculator;
        if (requestBookingDTO.getCouponCode() != null && !requestBookingDTO.getCouponCode().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(requestBookingDTO.getCouponCode())
                    .orElseThrow(() -> new EntityNotFoundException("Coupon with the given code does not exist."));
            // Additional coupon validations can be added here
            if (!coupon.isValid()) {
                throw new IllegalArgumentException("Coupon is not valid.");
            }

            priceCalculator = new PriceCalculator(requestBookingDTO.getNumAdults(),
                    requestBookingDTO.getNumChild(),
                    tour.getPriceAdult(),
                    tour.getPriceChild(),
                    coupon);
        } else {
            priceCalculator = new PriceCalculator(requestBookingDTO.getNumAdults(),
                    requestBookingDTO.getNumChild(),
                    tour.getPriceAdult(),
                    tour.getPriceChild());
        }

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

        // Set payment deadline
        // For testing: using minutes instead of hours (set booking.payment-deadline-hours to small value like 1)
        // In production: use plusHours(paymentDeadlineHours) with value like 24
        LocalDateTime paymentDeadline;
        if (paymentDeadlineHours <= 0) {
            // If 0 or negative, set deadline to 1 minute from now (for testing)
            paymentDeadline = LocalDateTime.now().plusMinutes(1);
        } else {
            paymentDeadline = LocalDateTime.now().plusHours(paymentDeadlineHours);
        }
        booking.setPaymentDeadline(paymentDeadline);

        logger.info("Created booking with payment deadline: {}", paymentDeadline);

        updateAvailableSlots(tourDeparture, requestBookingDTO.getNumAdults() + requestBookingDTO.getNumChild());

        bookingRepository.save(booking);
    }

    private void updateAvailableSlots(TourDeparture tourDeparture, int totalParticipants) {
        int updatedAvailableSlots = tourDeparture.getAvailableSlots() - totalParticipants;
        if (updatedAvailableSlots < 0) {
            throw new IllegalArgumentException("Not enough available slots for the selected tour departure.");
        }

        tourDeparture.setAvailableSlots(updatedAvailableSlots);
        tourDeparturesRepository.save(tourDeparture);
    }

    private TourDeparture validateTourDeparture(Long tourDepartureId) {
        TourDeparture tourDeparture = tourDeparturesRepository.findById(tourDepartureId)
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
