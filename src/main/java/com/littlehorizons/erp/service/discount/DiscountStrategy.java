package com.littlehorizons.erp.service.discount;

import java.math.BigDecimal;

/**
 * Strategy pattern: each implementation encapsulates one discount rule.
 * New rules are added by creating a new class, without modifying existing code (Open/Closed).
 */
public interface DiscountStrategy {

    /**
     * @param amount amount the discount is applied to
     * @param context payment context
     * @return discount value (never negative), rounded to 2 decimals
     */
    BigDecimal discountFor(BigDecimal amount, DiscountContext context);
}
