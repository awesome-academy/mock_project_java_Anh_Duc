package asterisk.sun.booking_tours.application.common.email;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.core.verification.VerificationTokenRepository;

/**
 * Scheduled task to clean up expired verification tokens
 */
@Component
public class VerificationTokenCleanupTask {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenCleanupTask(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    /**
     * Clean up expired tokens every day at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        verificationTokenRepository.deleteExpiredTokens(now);
        System.out.println("Cleaned up expired verification tokens at: " + now);
    }
}
