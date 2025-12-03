package asterisk.sun.booking_tours.core.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Query("SELECT b FROM BankAccount b WHERE " +
            "LOWER(b.bankName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.accountNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.accountHolder) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<BankAccount> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT b FROM BankAccount b WHERE b.isActive = :isActive ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<BankAccount> findByIsActive(@Param("isActive") Boolean isActive);

    @Query("SELECT b FROM BankAccount b ORDER BY b.displayOrder ASC, b.createdAt DESC")
    List<BankAccount> findAllOrderByDisplayOrder();

    boolean existsByAccountNumber(String accountNumber);
}
