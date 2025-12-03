package asterisk.sun.booking_tours.application.admin.coupon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import asterisk.sun.booking_tours.core.coupon.Coupon;
import asterisk.sun.booking_tours.core.coupon.CouponStatus;
import asterisk.sun.booking_tours.core.coupon.CouponType;

public class CouponResponseDTO {

    private Long id;
    private String code;
    private String description;
    private CouponType type;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPurchaseAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private CouponStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CouponResponseDTO() {}

    public CouponResponseDTO(Coupon coupon) {
        this.id = coupon.getId();
        this.code = coupon.getCode();
        this.description = coupon.getDescription();
        this.type = coupon.getType();
        this.discountValue = coupon.getDiscountValue();
        this.maxDiscountAmount = coupon.getMaxDiscountAmount();
        this.minPurchaseAmount = coupon.getMinPurchaseAmount();
        this.usageLimit = coupon.getUsageLimit();
        this.usedCount = coupon.getUsedCount();
        this.validFrom = coupon.getValidFrom();
        this.validTo = coupon.getValidTo();
        this.status = coupon.getStatus();
        this.createdAt = coupon.getCreatedAt();
        this.updatedAt = coupon.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public BigDecimal getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public void setStatus(CouponStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return status == CouponStatus.ACTIVE
                && (validFrom == null || validFrom.isBefore(now) || validFrom.isEqual(now))
                && (validTo == null || validTo.isAfter(now))
                && (usageLimit == null || usedCount < usageLimit);
    }
}
