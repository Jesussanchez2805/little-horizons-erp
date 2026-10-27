package com.littlehorizons.erp.service;

import com.littlehorizons.erp.exception.ClubEnrollmentException;
import com.littlehorizons.erp.exception.ClubFullException;
import com.littlehorizons.erp.exception.DuplicateMembershipException;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.service.discount.DiscountContext;
import com.littlehorizons.erp.service.discount.DiscountStrategy;
import com.littlehorizons.erp.service.discount.EarlyPaymentDiscountStrategy;
import com.littlehorizons.erp.service.discount.PriceCalculator;
import com.littlehorizons.erp.service.discount.ScholarshipDiscountStrategy;
import com.littlehorizons.erp.service.discount.SiblingDiscountStrategy;
import com.littlehorizons.erp.util.CufeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the practice exercises (statistics, late fees, discounts, club enrollment, CUFE, Student builder).
 * None of them needs a database connection.
 */
class PracticeExercisesTest {

    // ---------- Exercise 1: statistics with Streams ----------

    private AcademicRecord grade(int id, int studentId, int courseId, String courseName, String period, String value) {
        AcademicRecord r = new AcademicRecord(id, studentId, courseId, 1, 2026, period,
                value == null ? null : new BigDecimal(value), null, LocalDate.of(2026, 3, 1));
        r.setCourseName(courseName);
        return r;
    }

    private List<AcademicRecord> sampleGrades() {
        return List.of(
                grade(1, 1, 10, "Math", "I", "4.0"),
                grade(2, 2, 10, "Math", "I", "2.0"),
                grade(3, 1, 20, "Science", "I", "5.0"),
                grade(4, 2, 20, "Science", "I", "3.0"),
                grade(5, 3, 10, "Math", "I", "3.0"),
                grade(6, 3, 20, "Science", "I", "4.0"),
                grade(7, 3, 20, "Science", "II", null));
    }

    @Test
    @DisplayName("Average per course is grouped, sorted and ignores records without grade")
    void averageByCourse() {
        Map<String, Double> averages = new GradeStatisticsService().averageByCourse(sampleGrades());
        assertEquals(List.of("Math", "Science"), List.copyOf(averages.keySet()));
        assertEquals(3.0, averages.get("Math"), 0.0001);
        assertEquals(4.0, averages.get("Science"), 0.0001);
    }

    @Test
    @DisplayName("Top grades returns the best n records of a course and period")
    void topGrades() {
        List<AcademicRecord> top = new GradeStatisticsService().topGrades(sampleGrades(), 10, "I", 2);
        assertEquals(2, top.size());
        assertEquals(1, top.get(0).getId());
        assertEquals(5, top.get(1).getId());
        assertThrows(IllegalArgumentException.class,
                () -> new GradeStatisticsService().topGrades(sampleGrades(), 10, "I", -1));
    }

    @Test
    @DisplayName("Pass/fail partition always contains both keys")
    void passFailCount() {
        GradeStatisticsService service = new GradeStatisticsService();
        Map<Boolean, Long> counts = service.passFailCount(sampleGrades());
        assertEquals(5L, counts.get(true).longValue());
        assertEquals(1L, counts.get(false).longValue());
        assertEquals(0L, service.passFailCount(List.of()).get(false).longValue());
    }

    @Test
    @DisplayName("Summary and best student")
    void summaryAndBestStudent() {
        GradeStatisticsService service = new GradeStatisticsService();
        DoubleSummaryStatistics stats = service.summary(sampleGrades());
        assertEquals(6, stats.getCount());
        assertEquals(2.0, stats.getMin(), 0.0001);
        assertEquals(5.0, stats.getMax(), 0.0001);
        assertEquals(3.5, stats.getAverage(), 0.0001);
        assertEquals(1, service.bestStudentId(sampleGrades()).orElseThrow().intValue());
        assertTrue(service.bestStudentId(List.of()).isEmpty());
    }

    // ---------- Exercise 2: late fees ----------

    private StudentLoan loan(LocalDate expected, LocalDate actual) {
        StudentLoan l = new StudentLoan();
        l.setExpectedReturnDate(expected);
        l.setActualReturnDate(actual);
        return l;
    }

    @Test
    @DisplayName("Weekends and holidays are not counted as late days")
    void businessDaysLate() {
        LocalDate friday = LocalDate.of(2026, 10, 23);
        LocalDate wednesday = LocalDate.of(2026, 10, 28);
        assertEquals(3, new LoanFeeCalculator(Set.of()).countBusinessDaysLate(friday, wednesday));
        assertEquals(2, new LoanFeeCalculator(Set.of(LocalDate.of(2026, 10, 26)))
                .countBusinessDaysLate(friday, wednesday));
        assertEquals(0, new LoanFeeCalculator(Set.of()).countBusinessDaysLate(friday, friday));
        assertEquals(0, new LoanFeeCalculator(Set.of()).countBusinessDaysLate(wednesday, friday));
    }

    @Test
    @DisplayName("Fee depends on the category and is capped at 20 business days")
    void feeByCategory() {
        LoanFeeCalculator calculator = new LoanFeeCalculator(Set.of());
        StudentLoan returned = loan(LocalDate.of(2026, 10, 23), LocalDate.of(2026, 10, 28));
        assertEquals(new BigDecimal("1500"), calculator.calculateFee(returned, "Libro", LocalDate.of(2026, 12, 1)));
        assertEquals(new BigDecimal("15000"),
                calculator.calculateFee(returned, "Dispositivo Tecnológico", LocalDate.of(2026, 12, 1)));

        StudentLoan stillOut = loan(LocalDate.of(2026, 1, 2), null);
        assertEquals(new BigDecimal("10000"), calculator.calculateFee(stillOut, "Libro", LocalDate.of(2026, 3, 31)));

        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(returned, "Mobiliario", LocalDate.now()));
    }

    // ---------- Exercise 3: discounts with Strategy ----------

    private final LocalDate dueDate = LocalDate.of(2026, 11, 5);

    private PriceCalculator allDiscounts() {
        return new PriceCalculator(List.<DiscountStrategy>of(
                new SiblingDiscountStrategy(), new ScholarshipDiscountStrategy(), new EarlyPaymentDiscountStrategy()));
    }

    @Test
    @DisplayName("Discounts are applied one after another over the remaining amount")
    void sequentialDiscounts() {
        DiscountContext ctx = new DiscountContext(2, new BigDecimal("30"), LocalDate.of(2026, 10, 28), dueDate);
        assertEquals(new BigDecimal("598500.00"), allDiscounts().calculateFinalAmount(new BigDecimal("1000000"), ctx));
    }

    @Test
    @DisplayName("No discount applies when no rule matches")
    void noDiscount() {
        DiscountContext ctx = new DiscountContext(1, null, dueDate, dueDate);
        assertEquals(new BigDecimal("1000000.00"), allDiscounts().calculateFinalAmount(new BigDecimal("1000000"), ctx));
    }

    @Test
    @DisplayName("Three or more siblings get 15%")
    void threeSiblings() {
        PriceCalculator calculator = new PriceCalculator(List.of(new SiblingDiscountStrategy()));
        DiscountContext ctx = new DiscountContext(3, null, dueDate, dueDate);
        assertEquals(new BigDecimal("850000.00"), calculator.calculateFinalAmount(new BigDecimal("1000000"), ctx));
    }

    @Test
    @DisplayName("Combined discount is capped at 50% and invalid input is rejected")
    void capAndValidation() {
        DiscountContext full = new DiscountContext(1, new BigDecimal("100"), dueDate, dueDate);
        assertEquals(new BigDecimal("500000.00"), allDiscounts().calculateFinalAmount(new BigDecimal("1000000"), full));
        assertThrows(IllegalArgumentException.class,
                () -> allDiscounts().calculateFinalAmount(new BigDecimal("-1"), full));
        assertThrows(IllegalArgumentException.class, () -> new DiscountContext(0, null, dueDate, dueDate));
        assertThrows(IllegalArgumentException.class,
                () -> new DiscountContext(1, new BigDecimal("101"), dueDate, dueDate));
    }

    // ---------- Exercise 4: club enrollment with custom exceptions ----------

    private InMemoryExtracurricularDAO daoWithClub(int capacity, boolean active) {
        InMemoryExtracurricularDAO dao = new InMemoryExtracurricularDAO();
        dao.createClub(new Club(1, "Robotics", "Robotics club", null, capacity, "Tue 3pm", active));
        return dao;
    }

    @Test
    @DisplayName("Enrollment succeeds until the club is full")
    void enrollUntilFull() throws Exception {
        ClubEnrollmentService service = new ClubEnrollmentService(daoWithClub(2, true));
        ClubMembership first = service.enroll(1, 1, LocalDate.of(2026, 10, 26));
        assertEquals("Activa", first.getStatus());
        service.enroll(2, 1, LocalDate.of(2026, 10, 26));
        assertEquals(0, service.availableSpots(1));
        assertThrows(ClubFullException.class, () -> service.enroll(3, 1, LocalDate.of(2026, 10, 26)));
    }

    @Test
    @DisplayName("Duplicate, inactive and unknown clubs are rejected with the right exception")
    void rejectedEnrollments() throws Exception {
        ClubEnrollmentService service = new ClubEnrollmentService(daoWithClub(2, true));
        service.enroll(1, 1, LocalDate.of(2026, 10, 26));
        assertThrows(DuplicateMembershipException.class, () -> service.enroll(1, 1, LocalDate.of(2026, 10, 26)));
        assertThrows(ClubEnrollmentException.class, () -> service.enroll(1, 99, LocalDate.of(2026, 10, 26)));

        ClubEnrollmentService inactive = new ClubEnrollmentService(daoWithClub(5, false));
        ClubEnrollmentException ex = assertThrows(ClubEnrollmentException.class,
                () -> inactive.enroll(1, 1, LocalDate.of(2026, 10, 26)));
        assertEquals(ClubEnrollmentException.class, ex.getClass());
    }

    @Test
    @DisplayName("A withdrawn membership frees a place")
    void withdrawnFreesPlace() throws Exception {
        InMemoryExtracurricularDAO dao = daoWithClub(1, true);
        ClubEnrollmentService service = new ClubEnrollmentService(dao);
        ClubMembership m = service.enroll(1, 1, LocalDate.of(2026, 10, 26));
        assertThrows(ClubFullException.class, () -> service.enroll(2, 1, LocalDate.of(2026, 10, 26)));
        dao.withdrawMembership(m.getId(), "Changed activity");
        assertEquals(1, service.availableSpots(1));
        service.enroll(2, 1, LocalDate.of(2026, 10, 26));
    }

    // ---------- Exercise 5: CUFE with SHA-384 ----------

    @Test
    @DisplayName("CUFE is a deterministic 96-character SHA-384 hex string that can be verified")
    void cufeSha384() {
        LocalDate date = LocalDate.of(2026, 10, 26);
        String cufe = CufeGenerator.generate("FE-LH-000001", "901.458.789-2", date, new BigDecimal("650000"), "1017123001");
        assertEquals(96, cufe.length());
        assertEquals(cufe, CufeGenerator.generate("FE-LH-000001", "901.458.789-2", date, new BigDecimal("650000"), "1017123001"));
        assertTrue(CufeGenerator.verify(cufe, "FE-LH-000001", "901.458.789-2", date, new BigDecimal("650000"), "1017123001"));
        assertTrue(CufeGenerator.verify(cufe.toUpperCase(), "FE-LH-000001", "901.458.789-2", date, new BigDecimal("650000"), "1017123001"));
        assertFalse(CufeGenerator.verify(cufe, "FE-LH-000001", "901.458.789-2", date, new BigDecimal("650001"), "1017123001"));
        assertFalse(CufeGenerator.verify(null, "FE-LH-000001", "901.458.789-2", date, new BigDecimal("650000"), "1017123001"));
    }

    // ---------- Exercise 6: Student builder and age ----------

    @Test
    @DisplayName("Builder applies defaults and validates mandatory fields")
    void studentBuilder() {
        Student s = Student.builder()
                .documentType("TI").documentNumber("1100200300")
                .firstName("Valentina").firstLastName("Rojas")
                .birthDate(LocalDate.of(2015, 11, 1))
                .build();
        assertEquals("Activo", s.getStatus());
        assertNotNull(s.getEntryDate());
        assertEquals("Valentina Rojas", s.getFullName());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> Student.builder().documentType("TI").firstName("Valentina").build());
        assertTrue(ex.getMessage().contains("documentNumber"));
        assertTrue(ex.getMessage().contains("birthDate"));
    }

    @Test
    @DisplayName("Age counts complete years, including the birthday itself")
    void studentAge() {
        Student s = Student.builder().documentType("TI").documentNumber("1").firstName("A").firstLastName("B")
                .birthDate(LocalDate.of(2015, 11, 1)).build();
        assertEquals(10, s.getAge(LocalDate.of(2026, 10, 26)));
        assertEquals(11, s.getAge(LocalDate.of(2026, 11, 1)));
    }
}
