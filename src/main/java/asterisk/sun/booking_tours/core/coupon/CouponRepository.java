package asterisk.sun.booking_tours.core.coupon;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {

    Optional<Coupon> findByCode(String code);

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.status = :status")
    Optional<Coupon> findByCodeAndStatus(@Param("code") String code, @Param("status") CouponStatus status);

    Page<Coupon> findByStatus(CouponStatus status, Pageable pageable);

    @Query("SELECT c FROM Coupon c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Coupon> searchCoupons(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByCode(String code);
}
