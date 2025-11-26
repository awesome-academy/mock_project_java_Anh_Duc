package asterisk.sun.booking_tours.application.api.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {
    private final BigDecimal subTotal;
    private final BigDecimal discount;
    private final BigDecimal finalTotal;

    public PriceCalculator(Integer numAdults, Integer numChild,
            BigDecimal adultPrice, BigDecimal childPrice,
            BigDecimal discountPercent) {
        this.subTotal = calculateSubTotal(numAdults, numChild, adultPrice, childPrice);
        this.discount = calculateDiscount(this.subTotal, discountPercent);
        this.finalTotal = this.subTotal.subtract(this.discount);
    }

    private BigDecimal calculateSubTotal(Integer numAdults, Integer numChild,
            BigDecimal adultPrice, BigDecimal childPrice) {
        BigDecimal adultTotal = adultPrice.multiply(BigDecimal.valueOf(numAdults));
        BigDecimal childTotal = childPrice.multiply(BigDecimal.valueOf(numChild));
        return adultTotal.add(childTotal).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiscount(BigDecimal subTotal, BigDecimal discountPercent) {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return subTotal.multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }
}
