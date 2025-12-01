package asterisk.sun.booking_tours.core.verification;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asterisk.sun.booking_tours.core.user.User;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiryDate < ?1")
    void deleteExpiredTokens(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.user = ?1")
    void deleteByUser(User user);
}
