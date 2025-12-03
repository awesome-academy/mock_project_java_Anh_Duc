package asterisk.sun.booking_tours.application.api.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;

import asterisk.sun.booking_tours.core.coupon.Coupon;

public class PriceCalculator {
    private final BigDecimal subTotal;
    private final BigDecimal finalTotal;
    private final BigDecimal discountAmount;

    public PriceCalculator(Integer numAdults, Integer numChild,
            BigDecimal adultPrice, BigDecimal childPrice,
            Coupon coupon) {
        this.subTotal = calculateSubTotal(numAdults, numChild, adultPrice, childPrice);
        this.discountAmount = coupon.calculateDiscount(this.subTotal);
        this.finalTotal = this.subTotal.subtract(this.discountAmount);
    }

    public PriceCalculator(Integer numAdults, Integer numChild,
            BigDecimal adultPrice, BigDecimal childPrice) {
        this.subTotal = calculateSubTotal(numAdults, numChild, adultPrice, childPrice);
        this.finalTotal = this.subTotal;
        this.discountAmount = BigDecimal.ZERO;
    }

    private BigDecimal calculateSubTotal(Integer numAdults, Integer numChild,
            BigDecimal adultPrice, BigDecimal childPrice) {
        BigDecimal adultTotal = adultPrice.multiply(BigDecimal.valueOf(numAdults));
        BigDecimal childTotal = childPrice.multiply(BigDecimal.valueOf(numChild));
        return adultTotal.add(childTotal).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public BigDecimal getDiscount() {
        return discountAmount;
    }
}
