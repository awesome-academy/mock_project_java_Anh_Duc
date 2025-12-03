package asterisk.sun.booking_tours.application.api.payment;

import java.lang.ProcessHandle.Info;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.api.payment.dto.InforPaymentRequestDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.InforPaymentResponseDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.RequestPaymentDTO;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.payment.BankAccount;
import asterisk.sun.booking_tours.core.payment.BankAccountRepository;
import asterisk.sun.booking_tours.core.payment.Payment;
import asterisk.sun.booking_tours.core.payment.PaymentMethod;
import asterisk.sun.booking_tours.core.payment.PaymentRepository;
import asterisk.sun.booking_tours.core.payment.PaymentStatus;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    public PaymentService(PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public InforPaymentResponseDTO createPaymentInfo(InforPaymentRequestDTO request, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + userDetails.getUsername()));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking not found with id: " + request.getBookingId()));
        Tour tour = booking.getTourDeparture().getTour();

        BankAccount bankAccount = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bank account not found with id: " + request.getBankAccountId()));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(user);
        payment.setBankAccount(bankAccount);
        payment.setAmount(booking.getFinalTotal());
        payment.setPaymentMethod(PaymentMethod.INTERNET_BANKING);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());

        paymentRepository.save(payment);

        InforPaymentResponseDTO response = InforPaymentResponseDTO.builder()
                .bookingCode(booking.getCode())
                .tourName(tour.getName())
                .finalTotal(booking.getFinalTotal())
                .bankName(bankAccount.getBankName())
                .accountNumber(maskAccountNumber(bankAccount.getAccountNumber()))
                .accountHolderName(bankAccount.getAccountHolder())
                .transactionId(payment.getTransactionId())
                .build();

        return response;
    }

    @Transactional
    public void paymentBookingTour(RequestPaymentDTO requestPaymentDTO, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + userDetails.getUsername()));
        Booking booking = bookingRepository.findById(requestPaymentDTO.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking not found with id: " + requestPaymentDTO.getBookingId()));

        Payment payment = paymentRepository.findByTransactionId(requestPaymentDTO.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found with transaction ID: " + requestPaymentDTO.getTransactionId()));

        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setBankCode(requestPaymentDTO.getBankCode());

        paymentRepository.save(payment);

    }

    private String generateTransactionId() {
        String transactionId;
        do {
            transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (paymentRepository.existsByTransactionId(transactionId));
        return transactionId;
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        int length = accountNumber.length();
        return "*".repeat(length - 4) + accountNumber.substring(length - 4);
    }
}
