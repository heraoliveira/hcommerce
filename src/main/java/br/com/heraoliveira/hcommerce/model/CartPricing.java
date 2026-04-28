package br.com.heraoliveira.hcommerce.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CartPricing {
    private static final BigDecimal DISCOUNT_THRESHOLD_1 = new BigDecimal("200.00");
    private static final BigDecimal DISCOUNT_THRESHOLD_2 = new BigDecimal("500.00");
    private static final BigDecimal DISCOUNT_THRESHOLD_3 = new BigDecimal("1500.00");
    private static final BigDecimal DISCOUNT_PERCENTAGE_1 = new BigDecimal("10");
    private static final BigDecimal DISCOUNT_PERCENTAGE_2 = new BigDecimal("15");
    private static final BigDecimal DISCOUNT_PERCENTAGE_3 = new BigDecimal("20");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private CartPricing() {
    }

    public static PricingSummary calculate(Cart cart) {
        BigDecimal subtotal = cart.calculateSubtotal();
        BigDecimal discountPercentage = resolveDiscountPercentage(subtotal);
        BigDecimal discountAmount = calculateDiscountAmount(subtotal, discountPercentage);
        BigDecimal total = subtotal.subtract(discountAmount);

        return new PricingSummary(subtotal, discountPercentage, discountAmount, total);
    }

    private static BigDecimal calculateDiscountAmount(BigDecimal subtotal, BigDecimal discountPercentage) {
        return subtotal.multiply(discountPercentage)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal resolveDiscountPercentage(BigDecimal subtotal) {
        if (subtotal.compareTo(DISCOUNT_THRESHOLD_3) > 0) {
            return DISCOUNT_PERCENTAGE_3;
        }

        if (subtotal.compareTo(DISCOUNT_THRESHOLD_2) > 0) {
            return DISCOUNT_PERCENTAGE_2;
        }

        if (subtotal.compareTo(DISCOUNT_THRESHOLD_1) > 0) {
            return DISCOUNT_PERCENTAGE_1;
        }

        return BigDecimal.ZERO;
    }

    public record PricingSummary(
            BigDecimal subtotal,
            BigDecimal discountPercentage,
            BigDecimal discountAmount,
            BigDecimal total
    ) {
    }
}
