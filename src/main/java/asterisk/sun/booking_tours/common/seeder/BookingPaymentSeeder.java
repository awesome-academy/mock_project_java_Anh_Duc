package asterisk.sun.booking_tours.common.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.payment.Payment;
import asterisk.sun.booking_tours.core.payment.PaymentMethod;
import asterisk.sun.booking_tours.core.payment.PaymentRepository;
import asterisk.sun.booking_tours.core.payment.PaymentStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparture;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeder to create booking and payment data for testing report functionality.
 * This creates diverse data across different dates, statuses, and tours to
 * support:
 * - Daily Revenue Report
 * - Monthly Revenue Report
 * - Yearly Revenue Report
 * - Custom Period Revenue Report
 * - Booking Summary Report
 * - Tour Performance Report
 */
@Component
@Order(9)
public class BookingPaymentSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BookingPaymentSeeder.class);

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final TourDeparturesRepository tourDeparturesRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    // Vietnamese names for contacts
    private final String[][] contactNames = {
            { "Nguyễn", "Văn", "Minh" },
            { "Trần", "Thị", "Hương" },
            { "Lê", "Hoàng", "Nam" },
            { "Phạm", "Thu", "Thảo" },
            { "Hoàng", "Đức", "Anh" },
            { "Vũ", "Thị", "Lan" },
            { "Đặng", "Minh", "Tuấn" },
            { "Bùi", "Ngọc", "Mai" },
            { "Ngô", "Quốc", "Huy" },
            { "Dương", "Thu", "Hà" },
            { "Đinh", "Văn", "Long" },
            { "Võ", "Thị", "Linh" },
            { "Phan", "Thanh", "Tùng" },
            { "Trương", "Hồng", "Nhung" },
            { "Mai", "Xuân", "Phong" }
    };

    private final String[] phoneNumbers = {
            "0901234567", "0912345678", "0923456789", "0934567890", "0945678901",
            "0356789012", "0367890123", "0378901234", "0389012345", "0390123456",
            "0841234567", "0852345678", "0863456789", "0874567890", "0885678901"
    };

    public BookingPaymentSeeder(BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            TourDeparturesRepository tourDeparturesRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.tourDeparturesRepository = tourDeparturesRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedBookingsAndPayments();
    }

    private void seedBookingsAndPayments() {
        if (bookingRepository.count() == 0) {
            logger.info("Seeding bookings and payments for report testing...");

            // Use searchByKeyword with empty string to fetch departures with tours eagerly loaded
            List<TourDeparture> departures = tourDeparturesRepository.searchByKeyword("");
            List<User> clientUsers = userRepository.findByRole(Role.USER);

            if (departures.isEmpty()) {
                logger.warn("No tour departures found. Please seed tour departures first.");
                return;
            }

            if (clientUsers.isEmpty()) {
                logger.warn("No client users found. Please seed client users first.");
                return;
            }

            List<Booking> bookings = new ArrayList<>();
            List<Payment> payments = new ArrayList<>();
            LocalDate today = LocalDate.now();
            int bookingCodeCounter = 1;

            // ========================================
            // 1. Create bookings for DAILY report test
            // Last 30 days with varying amounts per day
            // ========================================
            logger.info("Creating bookings for daily report (last 30 days)...");
            for (int daysAgo = 0; daysAgo < 30; daysAgo++) {
                LocalDate bookingDate = today.minusDays(daysAgo);
                // Create 2-8 bookings per day
                int bookingsPerDay = 2 + random.nextInt(7);

                for (int j = 0; j < bookingsPerDay; j++) {
                    TourDeparture departure = departures.get(random.nextInt(departures.size()));
                    User user = clientUsers.get(random.nextInt(clientUsers.size()));

                    Booking booking = createBooking(departure, user, bookingDate, bookingCodeCounter++);

                    // 70% completed/paid, 20% pending, 10% cancelled
                    int statusRand = random.nextInt(100);
                    if (statusRand < 70) {
                        booking.setStatus(BookingStatus.COMPLETED);
                        bookings.add(booking);

                        // Create successful payment
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                        payments.add(payment);
                    } else if (statusRand < 90) {
                        booking.setStatus(BookingStatus.PENDING);
                        bookings.add(booking);
                    } else {
                        booking.setStatus(BookingStatus.CANCELLED);
                        booking.setCancellationReason("Customer request cancellation");
                        bookings.add(booking);
                    }
                }
            }

            // ========================================
            // 2. Create bookings for MONTHLY report test
            // Last 12 months with varying amounts per month
            // ========================================
            logger.info("Creating bookings for monthly report (last 12 months)...");
            for (int monthsAgo = 1; monthsAgo <= 12; monthsAgo++) {
                LocalDate monthDate = today.minusMonths(monthsAgo);
                // Create 20-50 bookings per month
                int bookingsPerMonth = 20 + random.nextInt(31);

                for (int j = 0; j < bookingsPerMonth; j++) {
                    // Random day within the month
                    int dayOfMonth = 1 + random.nextInt(Math.min(28, monthDate.lengthOfMonth()));
                    LocalDate bookingDate = monthDate.withDayOfMonth(dayOfMonth);

                    TourDeparture departure = departures.get(random.nextInt(departures.size()));
                    User user = clientUsers.get(random.nextInt(clientUsers.size()));

                    Booking booking = createBooking(departure, user, bookingDate, bookingCodeCounter++);

                    // 65% completed, 20% paid, 10% pending, 5% cancelled
                    int statusRand = random.nextInt(100);
                    if (statusRand < 65) {
                        booking.setStatus(BookingStatus.COMPLETED);
                        bookings.add(booking);
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                        payments.add(payment);
                    } else if (statusRand < 85) {
                        booking.setStatus(BookingStatus.PAID);
                        bookings.add(booking);
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                        payments.add(payment);
                    } else if (statusRand < 95) {
                        booking.setStatus(BookingStatus.PENDING);
                        bookings.add(booking);
                    } else {
                        booking.setStatus(BookingStatus.CANCELLED);
                        booking.setCancellationReason("Schedule conflict");
                        bookings.add(booking);
                    }
                }
            }

            // ========================================
            // 3. Create bookings for YEARLY report test
            // Last 3 years with varying amounts
            // ========================================
            logger.info("Creating bookings for yearly report (last 3 years)...");
            for (int yearsAgo = 1; yearsAgo <= 3; yearsAgo++) {
                LocalDate yearDate = today.minusYears(yearsAgo);
                // Create 100-200 bookings per year
                int bookingsPerYear = 100 + random.nextInt(101);

                for (int j = 0; j < bookingsPerYear; j++) {
                    // Random day within the year
                    int dayOfYear = 1 + random.nextInt(365);
                    LocalDate bookingDate = yearDate.withDayOfYear(Math.min(dayOfYear, yearDate.lengthOfYear()));

                    TourDeparture departure = departures.get(random.nextInt(departures.size()));
                    User user = clientUsers.get(random.nextInt(clientUsers.size()));

                    Booking booking = createBooking(departure, user, bookingDate, bookingCodeCounter++);

                    // Historical data - 80% completed, 15% cancelled, 5% refunded
                    int statusRand = random.nextInt(100);
                    if (statusRand < 80) {
                        booking.setStatus(BookingStatus.COMPLETED);
                        bookings.add(booking);
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                        payments.add(payment);
                    } else if (statusRand < 95) {
                        booking.setStatus(BookingStatus.CANCELLED);
                        booking.setCancellationReason("Historical cancellation");
                        bookings.add(booking);
                    } else {
                        booking.setStatus(BookingStatus.REFUNDED);
                        bookings.add(booking);
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.REFUNDED);
                        payments.add(payment);
                    }
                }
            }

            // ========================================
            // 4. Create high-value bookings for specific tours (Tour Performance)
            // To show different performance levels
            // ========================================
            logger.info("Creating high-value bookings for tour performance report...");
            // Pick top 5 tours to have higher booking rates
            int topToursCount = Math.min(5, departures.size());
            for (int i = 0; i < topToursCount; i++) {
                TourDeparture topDeparture = departures.get(i);

                // Create 30-50 extra bookings for top tours in last 6 months
                int extraBookings = 30 + random.nextInt(21);
                for (int j = 0; j < extraBookings; j++) {
                    int daysAgo = random.nextInt(180);
                    LocalDate bookingDate = today.minusDays(daysAgo);
                    User user = clientUsers.get(random.nextInt(clientUsers.size()));

                    Booking booking = createBooking(topDeparture, user, bookingDate, bookingCodeCounter++);
                    // High value tours with larger groups
                    booking.setNumAdults(2 + random.nextInt(4)); // 2-5 adults
                    booking.setNumChild(random.nextInt(3)); // 0-2 children
                    recalculateBookingTotal(booking, topDeparture);

                    booking.setStatus(BookingStatus.COMPLETED);
                    bookings.add(booking);

                    Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                    payments.add(payment);
                }
            }

            // ========================================
            // 5. Create some failed/pending payments for variety
            // ========================================
            logger.info("Creating failed and pending payments...");
            for (int i = 0; i < 20; i++) {
                int daysAgo = random.nextInt(60);
                LocalDate bookingDate = today.minusDays(daysAgo);
                TourDeparture departure = departures.get(random.nextInt(departures.size()));
                User user = clientUsers.get(random.nextInt(clientUsers.size()));

                Booking booking = createBooking(departure, user, bookingDate, bookingCodeCounter++);
                booking.setStatus(BookingStatus.PENDING);
                bookings.add(booking);

                // Create failed payment
                Payment failedPayment = createPayment(booking, user, bookingDate, PaymentStatus.FAILED);
                failedPayment.setNotes("Payment declined by bank");
                payments.add(failedPayment);
            }

            // ========================================
            // 6. Create some recent bookings for current week stats
            // ========================================
            logger.info("Creating recent bookings for current week stats...");
            for (int i = 0; i < 7; i++) {
                LocalDate bookingDate = today.minusDays(i);
                int bookingsPerDay = 3 + random.nextInt(5); // 3-7 per day

                for (int j = 0; j < bookingsPerDay; j++) {
                    TourDeparture departure = departures.get(random.nextInt(departures.size()));
                    User user = clientUsers.get(random.nextInt(clientUsers.size()));

                    Booking booking = createBooking(departure, user, bookingDate, bookingCodeCounter++);

                    // Recent bookings - mix of statuses
                    int statusRand = random.nextInt(100);
                    if (statusRand < 40) {
                        booking.setStatus(BookingStatus.COMPLETED);
                        bookings.add(booking);
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                        payments.add(payment);
                    } else if (statusRand < 70) {
                        booking.setStatus(BookingStatus.PAID);
                        bookings.add(booking);
                        Payment payment = createPayment(booking, user, bookingDate, PaymentStatus.COMPLETED);
                        payments.add(payment);
                    } else if (statusRand < 90) {
                        booking.setStatus(BookingStatus.CONFIRMED);
                        bookings.add(booking);
                    } else {
                        booking.setStatus(BookingStatus.PENDING);
                        bookings.add(booking);
                    }
                }
            }

            // Save all bookings first
            bookingRepository.saveAll(bookings);
            logger.info("Saved {} bookings", bookings.size());

            // Save all payments
            paymentRepository.saveAll(payments);
            logger.info("Saved {} payments", payments.size());

            // Calculate statistics
            long completedBookings = bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.COMPLETED || b.getStatus() == BookingStatus.PAID)
                    .count();
            long cancelledBookings = bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .count();
            BigDecimal totalRevenue = payments.stream()
                    .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            logger.info("=== Booking & Payment Seeding Summary ===");
            logger.info("Total Bookings: {}", bookings.size());
            logger.info("Completed/Paid Bookings: {}", completedBookings);
            logger.info("Cancelled Bookings: {}", cancelledBookings);
            logger.info("Total Payments: {}", payments.size());
            logger.info("Total Revenue: {} VND", totalRevenue);
            logger.info("==========================================");

        } else {
            logger.info("Bookings already exist. Skipping seeding.");
        }
    }

    private Booking createBooking(TourDeparture departure, User user, LocalDate bookingDate, int codeCounter) {
        Booking booking = new Booking();

        // Generate unique booking code
        String code = String.format("BK%06d", codeCounter);
        booking.setCode(code);

        booking.setTourDeparture(departure);
        booking.setUser(user);

        // Random number of travelers
        int numAdults = 1 + random.nextInt(4); // 1-4 adults
        int numChildren = random.nextInt(3); // 0-2 children
        booking.setNumAdults(numAdults);
        booking.setNumChild(numChildren);

        // Calculate prices based on tour
        BigDecimal adultPrice = null;
        BigDecimal childPrice = null;
        
        if (departure.getTour() != null) {
            adultPrice = departure.getTour().getPriceAdult();
            childPrice = departure.getTour().getPriceChild();
        }

        if (adultPrice == null) {
            adultPrice = BigDecimal.valueOf(1000000 + random.nextInt(5000000)); // 1M - 6M VND
        }
        if (childPrice == null) {
            childPrice = adultPrice.multiply(BigDecimal.valueOf(0.7)); // 70% of adult price
        }

        BigDecimal subTotal = adultPrice.multiply(BigDecimal.valueOf(numAdults))
                .add(childPrice.multiply(BigDecimal.valueOf(numChildren)));

        // Apply random discount (0-20%)
        int discountPercent = random.nextInt(21);
        BigDecimal discount = subTotal.multiply(BigDecimal.valueOf(discountPercent)).divide(BigDecimal.valueOf(100));
        BigDecimal finalTotal = subTotal.subtract(discount);

        booking.setSubTotal(subTotal);
        booking.setDiscount(discount);
        booking.setFinalTotal(finalTotal);

        // Set contact info
        String[] name = contactNames[random.nextInt(contactNames.length)];
        String contactName = name[0] + " " + name[1] + " " + name[2];
        booking.setContactName(contactName);
        booking.setContactPhone(phoneNumbers[random.nextInt(phoneNumbers.length)]);
        booking.setContactEmail(generateEmail(name));

        // Set payment deadline (24 hours after booking)
        LocalDateTime bookingDateTime = bookingDate.atTime(
                8 + random.nextInt(12), // 8:00 - 19:00
                random.nextInt(60));
        booking.setPaymentDeadline(bookingDateTime.plusHours(24));

        // Set notes
        if (random.nextInt(10) < 3) { // 30% have notes
            String[] notes = {
                    "Cần xe đón tại sân bay",
                    "Có trẻ nhỏ, cần ghế trẻ em",
                    "Yêu cầu phòng view biển",
                    "Dị ứng hải sản, cần thông báo nhà hàng",
                    "Muốn tham quan thêm địa điểm",
                    "Cần hướng dẫn viên nói tiếng Anh"
            };
            booking.setNotes(notes[random.nextInt(notes.length)]);
        }

        return booking;
    }

    private void recalculateBookingTotal(Booking booking, TourDeparture departure) {
        BigDecimal adultPrice = null;
        BigDecimal childPrice = null;
        
        if (departure.getTour() != null) {
            adultPrice = departure.getTour().getPriceAdult();
            childPrice = departure.getTour().getPriceChild();
        }

        if (adultPrice == null) {
            adultPrice = BigDecimal.valueOf(1000000 + random.nextInt(5000000));
        }
        if (childPrice == null) {
            childPrice = adultPrice.multiply(BigDecimal.valueOf(0.7));
        }

        BigDecimal subTotal = adultPrice.multiply(BigDecimal.valueOf(booking.getNumAdults()))
                .add(childPrice.multiply(BigDecimal.valueOf(booking.getNumChild())));

        int discountPercent = random.nextInt(21);
        BigDecimal discount = subTotal.multiply(BigDecimal.valueOf(discountPercent)).divide(BigDecimal.valueOf(100));
        BigDecimal finalTotal = subTotal.subtract(discount);

        booking.setSubTotal(subTotal);
        booking.setDiscount(discount);
        booking.setFinalTotal(finalTotal);
    }

    private Payment createPayment(Booking booking, User user, LocalDate paymentDate, PaymentStatus status) {
        Payment payment = new Payment();

        payment.setBooking(booking);
        payment.setUser(user);
        payment.setAmount(booking.getFinalTotal());
        payment.setStatus(status);

        // Random payment method
        PaymentMethod[] methods = PaymentMethod.values();
        payment.setPaymentMethod(methods[random.nextInt(methods.length)]);

        // Generate transaction ID
        String transactionId = "TXN" + paymentDate.toString().replace("-", "") +
                String.format("%06d", random.nextInt(1000000));
        payment.setTransactionId(transactionId);

        // Set bank code for bank transfers
        if (payment.getPaymentMethod() == PaymentMethod.BANK_TRANSFER ||
                payment.getPaymentMethod() == PaymentMethod.INTERNET_BANKING) {
            String[] bankCodes = { "VCB", "TCB", "ACB", "VIB", "MB", "TPB", "BIDV", "AGRI" };
            payment.setBankCode(bankCodes[random.nextInt(bankCodes.length)]);
        }

        // Set notes for some payments
        if (status == PaymentStatus.COMPLETED && random.nextInt(10) < 2) {
            payment.setNotes("Thanh toán thành công");
        } else if (status == PaymentStatus.FAILED) {
            String[] failNotes = {
                    "Số dư không đủ",
                    "Thẻ bị từ chối",
                    "Lỗi kết nối ngân hàng",
                    "Sai mã OTP"
            };
            payment.setNotes(failNotes[random.nextInt(failNotes.length)]);
        }

        return payment;
    }

    private String generateEmail(String[] name) {
        String firstName = removeDiacritics(name[2]).toLowerCase();
        String lastName = removeDiacritics(name[0]).toLowerCase();
        String[] domains = { "gmail.com", "yahoo.com", "hotmail.com", "outlook.com" };
        int randomNum = 100 + random.nextInt(900);
        return firstName + lastName + randomNum + "@" + domains[random.nextInt(domains.length)];
    }

    private String removeDiacritics(String str) {
        String[][] replacements = {
                { "à", "a" }, { "á", "a" }, { "ả", "a" }, { "ã", "a" }, { "ạ", "a" },
                { "ă", "a" }, { "ằ", "a" }, { "ắ", "a" }, { "ẳ", "a" }, { "ẵ", "a" }, { "ặ", "a" },
                { "â", "a" }, { "ầ", "a" }, { "ấ", "a" }, { "ẩ", "a" }, { "ẫ", "a" }, { "ậ", "a" },
                { "è", "e" }, { "é", "e" }, { "ẻ", "e" }, { "ẽ", "e" }, { "ẹ", "e" },
                { "ê", "e" }, { "ề", "e" }, { "ế", "e" }, { "ể", "e" }, { "ễ", "e" }, { "ệ", "e" },
                { "ì", "i" }, { "í", "i" }, { "ỉ", "i" }, { "ĩ", "i" }, { "ị", "i" },
                { "ò", "o" }, { "ó", "o" }, { "ỏ", "o" }, { "õ", "o" }, { "ọ", "o" },
                { "ô", "o" }, { "ồ", "o" }, { "ố", "o" }, { "ổ", "o" }, { "ỗ", "o" }, { "ộ", "o" },
                { "ơ", "o" }, { "ờ", "o" }, { "ớ", "o" }, { "ở", "o" }, { "ỡ", "o" }, { "ợ", "o" },
                { "ù", "u" }, { "ú", "u" }, { "ủ", "u" }, { "ũ", "u" }, { "ụ", "u" },
                { "ư", "u" }, { "ừ", "u" }, { "ứ", "u" }, { "ử", "u" }, { "ữ", "u" }, { "ự", "u" },
                { "ỳ", "y" }, { "ý", "y" }, { "ỷ", "y" }, { "ỹ", "y" }, { "ỵ", "y" },
                { "đ", "d" },
                { "À", "A" }, { "Á", "A" }, { "Ả", "A" }, { "Ã", "A" }, { "Ạ", "A" },
                { "Ă", "A" }, { "Ằ", "A" }, { "Ắ", "A" }, { "Ẳ", "A" }, { "Ẵ", "A" }, { "Ặ", "A" },
                { "Â", "A" }, { "Ầ", "A" }, { "Ấ", "A" }, { "Ẩ", "A" }, { "Ẫ", "A" }, { "Ậ", "A" },
                { "È", "E" }, { "É", "E" }, { "Ẻ", "E" }, { "Ẽ", "E" }, { "Ẹ", "E" },
                { "Ê", "E" }, { "Ề", "E" }, { "Ế", "E" }, { "Ể", "E" }, { "Ễ", "E" }, { "Ệ", "E" },
                { "Ì", "I" }, { "Í", "I" }, { "Ỉ", "I" }, { "Ĩ", "I" }, { "Ị", "I" },
                { "Ò", "O" }, { "Ó", "O" }, { "Ỏ", "O" }, { "Õ", "O" }, { "Ọ", "O" },
                { "Ô", "O" }, { "Ồ", "O" }, { "Ố", "O" }, { "Ổ", "O" }, { "Ỗ", "O" }, { "Ộ", "O" },
                { "Ơ", "O" }, { "Ờ", "O" }, { "Ớ", "O" }, { "Ở", "O" }, { "Ỡ", "O" }, { "Ợ", "O" },
                { "Ù", "U" }, { "Ú", "U" }, { "Ủ", "U" }, { "Ũ", "U" }, { "Ụ", "U" },
                { "Ư", "U" }, { "Ừ", "U" }, { "Ứ", "U" }, { "Ử", "U" }, { "Ữ", "U" }, { "Ự", "U" },
                { "Ỳ", "Y" }, { "Ý", "Y" }, { "Ỷ", "Y" }, { "Ỹ", "Y" }, { "Ỵ", "Y" },
                { "Đ", "D" }
        };

        String result = str;
        for (String[] replacement : replacements) {
            result = result.replace(replacement[0], replacement[1]);
        }
        return result;
    }
}
