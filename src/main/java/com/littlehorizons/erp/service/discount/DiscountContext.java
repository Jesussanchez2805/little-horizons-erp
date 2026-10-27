package com.littlehorizons.erp.service.discount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable data needed by the discount strategies.
 *
 * @param enrolledSiblings   children of the same guardian enrolled in the school, including this student (at least 1)
 * @param scholarshipPercent scholarship percentage between 0 and 100 (null is treated as 0)
 * @param paymentDate        date on which the payment is made
 * @param dueDate            payment due date
 */
public record DiscountContext(int enrolledSiblings, BigDecimal scholarshipPercent,
                              LocalDate paymentDate, LocalDate dueDate) {

    public DiscountContext {
        if (enrolledSiblings < 1) {
            throw new IllegalArgumentException("enrolledSiblings must be at least 1");
        }
        scholarshipPercent = scholarshipPercent == null ? BigDecimal.ZERO : scholarshipPercent;
        if (scholarshipPercent.signum() < 0 || scholarshipPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("scholarshipPercent must be between 0 and 100");
        }
        Objects.requireNonNull(paymentDate, "paymentDate");
        Objects.requireNonNull(dueDate, "dueDate");
    }
}
