package com.littlehorizons.erp.service.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Sibling discount: 0% with one enrolled child, 10% with two, 15% with three or more.
 */
public class SiblingDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal TWO_SIBLINGS_RATE = new BigDecimal("0.10");
    private static final BigDecimal THREE_OR_MORE_RATE = new BigDecimal("0.15");

    @Override
    public BigDecimal discountFor(BigDecimal amount, DiscountContext context) {
        BigDecimal rate = switch (context.enrolledSiblings()) {
            case 1 -> BigDecimal.ZERO;
            case 2 -> TWO_SIBLINGS_RATE;
            default -> THREE_OR_MORE_RATE;
        };
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
