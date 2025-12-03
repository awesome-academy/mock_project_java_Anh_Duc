package asterisk.sun.booking_tours.application.api.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.api.user.dto.UserProfileResponseDTO;
import asterisk.sun.booking_tours.application.api.user.dto.UserRegistrationRequestDTO;
import asterisk.sun.booking_tours.application.api.user.dto.UserRegistrationResponseDTO;
import asterisk.sun.booking_tours.application.api.user.dto.UserResendVerificationRequestDTO;
import asterisk.sun.booking_tours.application.common.email.EmailService;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;
import asterisk.sun.booking_tours.core.verification.VerificationToken;
import jakarta.mail.MessagingException;

@Service
public class ApiUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ApiUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public UserRegistrationResponseDTO registerUser(UserRegistrationRequestDTO requestDTO) {
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setAddress(requestDTO.getAddress() != null ? requestDTO.getAddress() : "");
        user.setRole(Role.USER);
        user.setStatus(UserStatus.PENDING);
        user.setIsVerified(false);

        User savedUser = userRepository.save(user);

        // Create verification token and send email
        try {
            VerificationToken verificationToken = emailService.createVerificationToken(savedUser);
            emailService.sendVerificationEmail(savedUser, verificationToken.getToken());
        } catch (MessagingException e) {
            // Log the error but don't fail the registration
            // You might want to use a proper logger here
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        return MapperHelper.map(savedUser, UserRegistrationResponseDTO.class);
    }

    public void createResendVerificationRequest(UserResendVerificationRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("User not found with email: " + requestDTO.getEmail()));

        if (user.getIsVerified()) {
            throw new IllegalStateException("User is already verified");
        }
        // Create new verification token and send email
        try {
            VerificationToken verificationToken = emailService.createVerificationToken(user);
            emailService.sendVerificationEmail(user, verificationToken.getToken());
        } catch (MessagingException e) {
            // Log the error but don't fail the process
            // You might want to use a proper logger here
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
    }

    @Transactional
    public void verifyEmailToken(String token) {
        boolean isValid = emailService.verifyToken(token);

        if (!isValid) {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }

        // Update user status to ACTIVE
        User user = emailService.getUserFromToken(token);
        if (user != null) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }

    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found with ID: " + userId));

        return MapperHelper.map(user, UserProfileResponseDTO.class);
    }
}
