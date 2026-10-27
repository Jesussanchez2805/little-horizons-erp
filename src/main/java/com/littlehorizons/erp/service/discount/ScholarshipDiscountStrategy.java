package com.littlehorizons.erp.service.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Scholarship discount: applies the percentage stored in the context.
 */
public class ScholarshipDiscountStrategy implements DiscountStrategy {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    @Override
    public BigDecimal discountFor(BigDecimal amount, DiscountContext context) {
        return amount.multiply(context.scholarshipPercent())
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
