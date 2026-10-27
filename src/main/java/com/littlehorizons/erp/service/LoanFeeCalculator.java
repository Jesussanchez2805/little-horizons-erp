package com.littlehorizons.erp.service;

import com.littlehorizons.erp.model.StudentLoan;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Calculates late fees for overdue resource loans.
 * <ul>
 *   <li>Only business days count (Saturdays, Sundays and the configured holidays are skipped).</li>
 *   <li>The daily rate depends on the resource category.</li>
 *   <li>At most {@value #MAX_BILLABLE_DAYS} business days are billed.</li>
 * </ul>
 */
public class LoanFeeCalculator {

    public static final int MAX_BILLABLE_DAYS = 20;

    /** Daily late fee (COP) per resource category. */
    public enum FeeRate {
        BOOK("Libro", 500),
        LAB_EQUIPMENT("Equipo de Laboratorio", 3000),
        SPORTS_EQUIPMENT("Equipo Deportivo", 1500),
        TECH_DEVICE("Dispositivo Tecnológico", 5000);

        private final String categoryName;
        private final BigDecimal dailyRate;

        FeeRate(String categoryName, long dailyRate) {
            this.categoryName = categoryName;
            this.dailyRate = BigDecimal.valueOf(dailyRate);
        }

        public BigDecimal getDailyRate() { return dailyRate; }

        public static FeeRate fromCategoryName(String categoryName) {
            if (categoryName == null) {
                throw new IllegalArgumentException("Category name must not be null");
            }
            return Arrays.stream(values())
                    .filter(rate -> matchesCategory(rate.categoryName, categoryName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown resource category: " + categoryName));
        }

        private static boolean matchesCategory(String standard, String input) {
            if (standard.equalsIgnoreCase(input)) return true;
            String sNorm = standard.toLowerCase().replace("ó", "o").replace("á", "a").replace("é", "e").replace("í", "i").replace("ú", "u");
            String iNorm = input.toLowerCase().replace("ó", "o").replace("á", "a").replace("é", "e").replace("í", "i").replace("ú", "u").replace("a3", "o").replace("ã³", "o");
            return sNorm.equalsIgnoreCase(iNorm);
        }
    }

    private final Set<LocalDate> holidays;

    public LoanFeeCalculator(Set<LocalDate> holidays) {
        this.holidays = Set.copyOf(holidays);
    }

    /**
     * Business days elapsed after {@code expectedReturnDate} up to and including {@code endDate}.
     * Returns 0 when the resource was returned on time or early.
     */
    public int countBusinessDaysLate(LocalDate expectedReturnDate, LocalDate endDate) {
        Objects.requireNonNull(expectedReturnDate, "expectedReturnDate");
        Objects.requireNonNull(endDate, "endDate");
        if (!endDate.isAfter(expectedReturnDate)) {
            return 0;
        }
        return (int) expectedReturnDate.plusDays(1)
                .datesUntil(endDate.plusDays(1))
                .filter(this::isBusinessDay)
                .count();
    }

    /**
     * Fee owed for a student loan. When the loan has not been returned yet,
     * {@code today} is used as the end date.
     */
    public BigDecimal calculateFee(StudentLoan loan, String categoryName, LocalDate today) {
        Objects.requireNonNull(loan, "loan");
        FeeRate rate = FeeRate.fromCategoryName(categoryName);
        LocalDate endDate = loan.getActualReturnDate() != null ? loan.getActualReturnDate() : today;
        int billableDays = Math.min(countBusinessDaysLate(loan.getExpectedReturnDate(), endDate), MAX_BILLABLE_DAYS);
        return rate.getDailyRate().multiply(BigDecimal.valueOf(billableDays));
    }

    private boolean isBusinessDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && !holidays.contains(date);
    }
}
