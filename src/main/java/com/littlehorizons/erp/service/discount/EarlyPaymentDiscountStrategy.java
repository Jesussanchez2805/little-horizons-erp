package com.littlehorizons.erp.service.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Early payment discount: 5% when the payment is made at least 5 days before the due date.
 */
public class EarlyPaymentDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.05");
    private static final int MIN_DAYS_IN_ADVANCE = 5;

    @Override
    public BigDecimal discountFor(BigDecimal amount, DiscountContext context) {
        boolean paidEarly = !context.paymentDate().isAfter(context.dueDate().minusDays(MIN_DAYS_IN_ADVANCE));
        return paidEarly
                ? amount.multiply(RATE).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
    }
}
