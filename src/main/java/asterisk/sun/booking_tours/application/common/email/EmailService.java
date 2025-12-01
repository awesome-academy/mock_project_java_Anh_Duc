package asterisk.sun.booking_tours.application.common.email;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.verification.VerificationToken;
import asterisk.sun.booking_tours.core.verification.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationTokenRepository verificationTokenRepository;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.verification-token-expiry:24}")
    private int tokenExpiryHours;

    public EmailService(JavaMailSender mailSender,
            VerificationTokenRepository verificationTokenRepository,
            TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.verificationTokenRepository = verificationTokenRepository;
        this.templateEngine = templateEngine;
    }

    /**
     * Create verification token for user
     */
    @Transactional
    public VerificationToken createVerificationToken(User user) {
        // Delete any existing tokens for this user
        verificationTokenRepository.deleteByUser(user);

        // Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenExpiryHours);

        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        return verificationTokenRepository.save(verificationToken);
    }

    /**
     * Send verification email to user
     */
    public void sendVerificationEmail(User user, String token) throws MessagingException {
        String verificationUrl = baseUrl + "/api/v1/users/verify-email?token=" + token;

        String subject = "Xác nhận tài khoản - Booking Tours";
        String htmlContent = buildVerificationEmailContent(user, verificationUrl);

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(fromEmail);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Build verification email HTML content using Thymeleaf
     */
    private String buildVerificationEmailContent(User user, String verificationUrl) {
        Context context = new Context();
        context.setVariable("firstName", user.getFirstName());
        context.setVariable("lastName", user.getLastName());
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("tokenExpiryHours", tokenExpiryHours);

        return templateEngine.process("email/email-verification", context);
    }

    /**
     * Verify token
     */
    @Transactional
    public boolean verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElse(null);

        if (verificationToken == null) {
            return false;
        }

        if (verificationToken.isExpired()) {
            return false;
        }

        if (verificationToken.getVerified()) {
            return false; // Already verified
        }

        // Mark token as verified
        verificationToken.setVerified(true);
        verificationToken.setVerifiedAt(LocalDateTime.now());
        verificationTokenRepository.save(verificationToken);

        // Update user status
        User user = verificationToken.getUser();
        user.setIsVerified(true);
        // User will be updated by cascade or you need to save it

        return true;
    }

    /**
     * Get user from verification token
     */
    public User getUserFromToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .map(VerificationToken::getUser)
                .orElse(null);
    }
}
