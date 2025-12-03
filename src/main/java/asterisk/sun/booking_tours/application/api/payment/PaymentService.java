package asterisk.sun.booking_tours.application.api.payment;

import java.lang.ProcessHandle.Info;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.api.payment.dto.InforPaymentRequestDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.InforPaymentResponseDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.PaymentResponseDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.RequestPaymentDTO;
import asterisk.sun.booking_tours.application.api.payment.dto.UpdatePaymentStatusDTO;
import asterisk.sun.booking_tours.application.api.tour.dto.ViewTourDeparturesResponseDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.booking.Booking;
import asterisk.sun.booking_tours.core.booking.BookingRepository;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
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

    public InforPaymentResponseDTO getInfoPaymentForBooking(InforPaymentRequestDTO request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking not found with id: " + request.getBookingId()));
        Tour tour = booking.getTourDeparture().getTour();

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        return InforPaymentResponseDTO.builder()
                .departureDate(booking.getTourDeparture().getDepartureDate())
                .returnDate(booking.getTourDeparture().getReturnDate())
                .durationDays(booking.getTourDeparture().getTour().getDurationDays())
                .numAdults(booking.getNumAdults())
                .numChildren(booking.getNumChild())
                .finalTotal(booking.getFinalTotal())
                .notes(booking.getNotes())
                .contactName(booking.getContactName())
                .contactPhone(booking.getContactPhone())
                .tourName(tour.getName())
                .tourDescription(tour.getDescription())
                .bankAccounts(bankAccounts)
                .build();
    }

    /**
     * Create a new payment for a booking with internet banking
     */
    @Transactional
    public void createPayment(RequestPaymentDTO requestPaymentDTO) {
        // Validate booking exists and is in pending status
        Booking booking = bookingRepository.findById(requestPaymentDTO.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Booking not found with id: " + requestPaymentDTO.getBookingId()));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Payment can only be made for bookings in PENDING status");
        }

        // Validate user exists
        User user = userRepository.findById(requestPaymentDTO.getUserId())
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with id: " + requestPaymentDTO.getUserId()));

        // Check if booking belongs to user
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Booking does not belong to the specified user");
        }

        // Validate payment amount matches booking final total
        if (requestPaymentDTO.getAmount().compareTo(booking.getFinalTotal()) != 0) {
            throw new IllegalArgumentException("Payment amount must match booking total");
        }

        // Create payment entity
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUser(user);
        payment.setAmount(requestPaymentDTO.getAmount());
        payment.setPaymentMethod(requestPaymentDTO.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);

        // Generate unique transaction ID
        String transactionId = generateTransactionId();
        payment.setTransactionId(transactionId);

        // Build notes with banking details
        StringBuilder notes = new StringBuilder();
        if (requestPaymentDTO.getNotes() != null && !requestPaymentDTO.getNotes().isEmpty()) {
            notes.append(requestPaymentDTO.getNotes()).append(" | ");
        }

        if (requestPaymentDTO.getPaymentMethod() == PaymentMethod.INTERNET_BANKING) {
            notes.append("Bank: ")
                    .append(requestPaymentDTO.getBankCode() != null ? requestPaymentDTO.getBankCode() : "N/A");
        }

        payment.setNotes(notes.toString());

        // Save payment
        paymentRepository.save(payment);

        // Update booking status to confirmed when payment is initiated
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    /**
     * Get payment by ID
     */
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));
        return convertToResponseDTO(payment, null);
    }

    /**
     * Get payment by transaction ID
     */
    public PaymentResponseDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Payment not found with transaction id: " + transactionId));
        return convertToResponseDTO(payment, null);
    }

    /**
     * Get all payments for a booking
     */
    public List<PaymentResponseDTO> getPaymentsByBookingId(Long bookingId) {
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        return payments.stream()
                .map(payment -> convertToResponseDTO(payment, null))
                .collect(Collectors.toList());
    }

    /**
     * Get all payments for a user
     */
    public List<PaymentResponseDTO> getPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(payment -> convertToResponseDTO(payment, null))
                .collect(Collectors.toList());
    }

    /**
     * Update payment status (for webhook callbacks or manual updates)
     */
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(UpdatePaymentStatusDTO updatePaymentStatusDTO) {
        Payment payment = paymentRepository.findById(updatePaymentStatusDTO.getPaymentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found with id: " + updatePaymentStatusDTO.getPaymentId()));

        payment.setStatus(updatePaymentStatusDTO.getStatus());

        // Update notes if provided
        if (updatePaymentStatusDTO.getNotes() != null && !updatePaymentStatusDTO.getNotes().isEmpty()) {
            String existingNotes = payment.getNotes() != null ? payment.getNotes() : "";
            payment.setNotes(existingNotes + " | Status updated: " + updatePaymentStatusDTO.getNotes());
        }

        Payment updatedPayment = paymentRepository.save(payment);

        // Update booking status based on payment status
        Booking booking = payment.getBooking();
        if (updatePaymentStatusDTO.getStatus() == PaymentStatus.COMPLETED) {
            booking.setStatus(BookingStatus.PAID);
            bookingRepository.save(booking);
        } else if (updatePaymentStatusDTO.getStatus() == PaymentStatus.FAILED ||
                updatePaymentStatusDTO.getStatus() == PaymentStatus.CANCELLED) {
            // Only revert to pending if it was confirmed but not paid
            if (booking.getStatus() == BookingStatus.CONFIRMED) {
                booking.setStatus(BookingStatus.PENDING);
                bookingRepository.save(booking);
            }
        }

        return convertToResponseDTO(updatedPayment, null);
    }

    /**
     * Verify payment (simulate bank verification)
     */
    @Transactional
    public PaymentResponseDTO verifyPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Payment cannot be verified in current status: " + payment.getStatus());
        }

        // Simulate verification process
        payment.setStatus(PaymentStatus.PROCESSING);

        // In real implementation, this would call bank API to verify
        // For now, we'll mark it as completed
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setNotes(payment.getNotes() + " | Verified at: " + LocalDateTime.now());

        Payment verifiedPayment = paymentRepository.save(payment);

        // Update booking status
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);

        return convertToResponseDTO(verifiedPayment, null);
    }

    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        String transactionId;
        do {
            transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (paymentRepository.existsByTransactionId(transactionId));
        return transactionId;
    }

    /**
     * Convert Payment entity to PaymentResponseDTO
     */
    private PaymentResponseDTO convertToResponseDTO(Payment payment, RequestPaymentDTO requestDTO) {
        PaymentResponseDTO responseDTO = new PaymentResponseDTO();
        responseDTO.setId(payment.getId());
        responseDTO.setBookingId(payment.getBooking().getId());
        responseDTO.setBookingCode(payment.getBooking().getCode());
        responseDTO.setUserId(payment.getUser().getId());
        responseDTO.setAmount(payment.getAmount());
        responseDTO.setPaymentMethod(payment.getPaymentMethod());
        responseDTO.setTransactionId(payment.getTransactionId());
        responseDTO.setStatus(payment.getStatus());
        responseDTO.setNotes(payment.getNotes());
        responseDTO.setCreatedAt(payment.getCreatedAt());
        responseDTO.setUpdatedAt(payment.getUpdatedAt());

        // Add banking details if available
        if (requestDTO != null && requestDTO.getPaymentMethod() == PaymentMethod.INTERNET_BANKING) {
            responseDTO.setBankCode(requestDTO.getBankCode());

            responseDTO.setPaymentUrl(generatePaymentUrl(payment.getTransactionId(), requestDTO.getBankCode()));
        }

        return responseDTO;
    }

    /**
     * Mask account number for security (show only last 4 digits)
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        int length = accountNumber.length();
        return "*".repeat(length - 4) + accountNumber.substring(length - 4);
    }

    /**
     * Generate payment URL (mock implementation)
     */
    private String generatePaymentUrl(String transactionId, String bankCode) {
        // In real implementation, this would integrate with payment gateway
        return "https://payment-gateway.example.com/pay?txn=" + transactionId + "&bank=" +
                (bankCode != null ? bankCode : "default");
    }
}
