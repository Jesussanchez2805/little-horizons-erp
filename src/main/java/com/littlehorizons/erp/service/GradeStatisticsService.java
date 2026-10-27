package com.littlehorizons.erp.service;

import com.littlehorizons.erp.model.AcademicRecord;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Grade statistics computed exclusively with lambdas and the Stream API.
 * Records without a grade value are ignored by every operation.
 */
public class GradeStatisticsService {

    public static final BigDecimal PASSING_GRADE = new BigDecimal("3.0");

    /**
     * Average grade per course, sorted alphabetically by course name.
     * Falls back to "Course {id}" when the course name is not loaded.
     */
    public Map<String, Double> averageByCourse(List<AcademicRecord> records) {
        return graded(records).stream()
                .collect(Collectors.groupingBy(
                        this::courseLabel,
                        TreeMap::new,
                        Collectors.averagingDouble(r -> r.getGradeValue().doubleValue())));
    }

    /**
     * The {@code n} highest grades of a course in a given period, best first.
     */
    public List<AcademicRecord> topGrades(List<AcademicRecord> records, int courseId, String period, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must not be negative");
        }
        return graded(records).stream()
                .filter(r -> r.getCourseId() == courseId)
                .filter(r -> period.equals(r.getAcademicPeriod()))
                .sorted(Comparator.comparing(AcademicRecord::getGradeValue).reversed())
                .limit(n)
                .toList();
    }

    /**
     * Number of passing (true) and failing (false) grades. Both keys are always present.
     */
    public Map<Boolean, Long> passFailCount(List<AcademicRecord> records) {
        return graded(records).stream()
                .collect(Collectors.partitioningBy(
                        r -> r.getGradeValue().compareTo(PASSING_GRADE) >= 0,
                        Collectors.counting()));
    }

    /**
     * Count, min, max, average and sum of every grade.
     */
    public DoubleSummaryStatistics summary(List<AcademicRecord> records) {
        return graded(records).stream()
                .mapToDouble(r -> r.getGradeValue().doubleValue())
                .summaryStatistics();
    }

    /**
     * Id of the student with the highest overall average. Ties are resolved in favor
     * of the lowest student id. Empty when there are no graded records.
     */
    public Optional<Integer> bestStudentId(List<AcademicRecord> records) {
        return graded(records).stream()
                .collect(Collectors.groupingBy(
                        AcademicRecord::getStudentId,
                        Collectors.averagingDouble(r -> r.getGradeValue().doubleValue())))
                .entrySet().stream()
                .max(Map.Entry.<Integer, Double>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .map(Map.Entry::getKey);
    }

    private List<AcademicRecord> graded(List<AcademicRecord> records) {
        return records.stream().filter(r -> r.getGradeValue() != null).toList();
    }

    private String courseLabel(AcademicRecord record) {
        String name = record.getCourseName();
        return (name != null && !name.isBlank()) ? name : "Course " + record.getCourseId();
    }
}
