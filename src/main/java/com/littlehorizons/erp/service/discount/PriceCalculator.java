package com.littlehorizons.erp.service.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Applies a list of discount strategies in order, each one over the amount that remains
 * after the previous one. The combined discount can never exceed {@link #MAX_TOTAL_DISCOUNT}
 * of the base amount.
 */
public class PriceCalculator {

    public static final BigDecimal MAX_TOTAL_DISCOUNT = new BigDecimal("0.50");

    private final List<DiscountStrategy> strategies;

    public PriceCalculator(List<DiscountStrategy> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    public BigDecimal calculateFinalAmount(BigDecimal baseAmount, DiscountContext context) {
        if (baseAmount == null || baseAmount.signum() < 0) {
            throw new IllegalArgumentException("baseAmount must be zero or positive");
        }

        BigDecimal remaining = baseAmount;
        for (DiscountStrategy strategy : strategies) {
            remaining = remaining.subtract(strategy.discountFor(remaining, context));
        }

        BigDecimal minimumAllowed = baseAmount.multiply(BigDecimal.ONE.subtract(MAX_TOTAL_DISCOUNT));
        if (remaining.compareTo(minimumAllowed) < 0) {
            remaining = minimumAllowed;
        }
        return remaining.setScale(2, RoundingMode.HALF_UP);
    }
}
