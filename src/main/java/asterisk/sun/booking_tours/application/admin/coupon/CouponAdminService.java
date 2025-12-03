package asterisk.sun.booking_tours.application.admin.coupon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.application.admin.coupon.dto.CouponRequestDTO;
import asterisk.sun.booking_tours.application.admin.coupon.dto.CouponResponseDTO;
import asterisk.sun.booking_tours.application.admin.coupon.dto.CouponSearchRequestDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.coupon.Coupon;
import asterisk.sun.booking_tours.core.coupon.CouponRepository;
import asterisk.sun.booking_tours.core.coupon.CouponStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

@Service
public class CouponAdminService extends BaseServiceController<CouponRepository> {

    public CouponAdminService(CouponRepository couponRepository) {
        super(couponRepository);
    }

    public Page<CouponResponseDTO> searchCoupons(CouponSearchRequestDTO request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by("createdAt").descending());

        Specification<Coupon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo keyword
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likeKey = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("code")), likeKey),
                        cb.like(cb.lower(root.get("description")), likeKey)));
            }

            // Lọc theo status
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                try {
                    CouponStatus status = CouponStatus.valueOf(request.getStatus());
                    predicates.add(cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                    // Bỏ qua nếu status không hợp lệ
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Coupon> pageResult = repository.findAll(spec, pageable);
        return pageResult.map(CouponResponseDTO::new);
    }

    @Transactional
    public CouponResponseDTO createCoupon(CouponRequestDTO request) {
        // Kiểm tra code đã tồn tại chưa
        if (repository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists: " + request.getCode());
        }

        Coupon coupon = MapperHelper.map(request, Coupon.class);
        coupon.setUsedCount(0);

        Coupon savedCoupon = repository.save(coupon);
        return new CouponResponseDTO(savedCoupon);
    }

    public CouponResponseDTO getCouponById(Long id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
        return new CouponResponseDTO(coupon);
    }

    @Transactional
    public CouponResponseDTO updateCoupon(Long id, CouponRequestDTO request) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));

        // Kiểm tra nếu code mới đã tồn tại (ngoại trừ coupon hiện tại)
        if (!coupon.getCode().equals(request.getCode()) && repository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Coupon code already exists: " + request.getCode());
        }

        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());
        coupon.setType(request.getType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setMinPurchaseAmount(request.getMinPurchaseAmount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidTo(request.getValidTo());
        coupon.setStatus(request.getStatus());

        Coupon updatedCoupon = repository.save(coupon);
        return new CouponResponseDTO(updatedCoupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + id));
        repository.delete(coupon);
    }

    public CouponResponseDTO findByCode(String code) {
        Coupon coupon = repository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with code: " + code));
        return new CouponResponseDTO(coupon);
    }

    public CouponResponseDTO validateAndGetCoupon(String code) {
        Coupon coupon = repository.findByCodeAndStatus(code, CouponStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive coupon code"));

        if (!coupon.isValid()) {
            throw new IllegalArgumentException("Coupon is not valid or has expired");
        }

        return new CouponResponseDTO(coupon);
    }

    @Transactional
    public void incrementCouponUsage(Long couponId) {
        Coupon coupon = repository.findById(couponId)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + couponId));

        coupon.incrementUsedCount();
        repository.save(coupon);
    }

    public List<CouponResponseDTO> getActiveCoupons() {
        Page<Coupon> activeCoupons = repository.findByStatus(
                CouponStatus.ACTIVE,
                PageRequest.of(0, 100, Sort.by("createdAt").descending()));
        return activeCoupons.map(CouponResponseDTO::new).getContent();
    }

    @Transactional
    public void updateExpiredCoupons() {
        // Tự động cập nhật status của các coupon đã hết hạn
        List<Coupon> allCoupons = repository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Coupon coupon : allCoupons) {
            if (coupon.getStatus() == CouponStatus.ACTIVE &&
                    coupon.getValidTo() != null &&
                    coupon.getValidTo().isBefore(now)) {
                coupon.setStatus(CouponStatus.EXPIRED);
                repository.save(coupon);
            }
        }
    }

    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
}
