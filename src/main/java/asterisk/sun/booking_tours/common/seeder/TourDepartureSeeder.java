package asterisk.sun.booking_tours.common.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDepartureStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(4)
public class TourDepartureSeeder implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(TourDepartureSeeder.class);
    private final TourDeparturesRepository tourDeparturesRepository;
    private final TourRepository tourRepository;
    private final Random random = new Random();

    public TourDepartureSeeder(TourDeparturesRepository tourDeparturesRepository, TourRepository tourRepository) {
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.tourRepository = tourRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedTourDepartures();
    }

    private void seedTourDepartures() {
        if (tourDeparturesRepository.count() == 0) {
            logger.info("Seeding tour departures...");

            List<Tour> tours = tourRepository.findAll();

            if (tours.isEmpty()) {
                logger.warn("No tours found. Please seed tours first.");
                return;
            }

            List<TourDeparture> departures = new ArrayList<>();
            LocalDate today = LocalDate.now();

            // Tạo 3-5 lịch khởi hành cho mỗi tour
            for (Tour tour : tours) {
                int numberOfDepartures = 3 + random.nextInt(3); // 3-5 lịch khởi hành

                for (int i = 0; i < numberOfDepartures; i++) {
                    TourDeparture departure = new TourDeparture();
                    departure.setTour(tour);

                    // Random ngày khởi hành trong vòng 6 tháng tới
                    int daysFromNow = 7 + random.nextInt(180); // Từ 7 ngày đến 6 tháng
                    LocalDate departureDate = today.plusDays(daysFromNow);
                    departure.setDepartureDate(departureDate);

                    // Tính ngày về dựa trên duration của tour
                    int durationDays = tour.getDurationDays() != null ? tour.getDurationDays() : 3;
                    LocalDate returnDate = departureDate.plusDays(durationDays - 1);
                    departure.setReturnDate(returnDate);

                    // Random số chỗ từ 20-50
                    int totalSlots = 20 + random.nextInt(31);
                    departure.setTotalSlots(totalSlots);

                    // Random số chỗ còn trống (50%-100% của total)
                    int availableSlots = totalSlots / 2 + random.nextInt(totalSlots / 2 + 1);
                    departure.setAvailableSlots(availableSlots);

                    // Set status dựa trên ngày khởi hành và số chỗ
                    TourDepartureStatus status;
                    if (departureDate.isBefore(today)) {
                        status = TourDepartureStatus.COMPLETED;
                    } else if (availableSlots == 0) {
                        status = TourDepartureStatus.FULL;
                    } else if (daysFromNow < 30) {
                        status = TourDepartureStatus.CONFIRMED;
                    } else {
                        status = TourDepartureStatus.SCHEDULED;
                    }
                    departure.setStatus(status);

                    departures.add(departure);
                }
            }

            tourDeparturesRepository.saveAll(departures);
            logger.info("Seeded {} tour departures successfully!", departures.size());
        } else {
            logger.info("Tour departures already exist. Skipping seeding.");
        }
    }
}
