package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.AcademicDAO;
import com.littlehorizons.erp.dao.AcademicDAOImpl;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.AppLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller orchestrating academic processes: Enrollments, Grade Management,
 * GPA calculations using Stream API, and Attendance tracking.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class AcademicController {

    private final AcademicDAO academicDAO;

    public AcademicController() {
        this(new AcademicDAOImpl());
    }

    public AcademicController(AcademicDAO academicDAO) {
        this.academicDAO = academicDAO;
    }

    // --- Grade Levels & Courses ---

    public List<GradeLevel> getAllGradeLevels() {
        return academicDAO.findAllGradeLevels();
    }

    public List<Course> getAllCourses() {
        return academicDAO.findAllCourses();
    }

    public List<Course> getCoursesByGrade(int gradeId) {
        return academicDAO.findCoursesByGrade(gradeId);
    }

    public Optional<Course> getCourseById(int courseId) {
        return academicDAO.findCourseById(courseId);
    }

    public Optional<Course> createCourse(Course course) {
        return Optional.ofNullable(academicDAO.createCourse(course));
    }

    // --- Enrollments ---

    public Optional<Enrollment> enrollStudent(int studentId, int gradeId, int year, String notes) {
        Optional<Enrollment> existing = academicDAO.findEnrollmentByStudentAndYear(studentId, year);
        if (existing.isPresent()) {
            AppLogger.warning("Student " + studentId + " is already enrolled for academic year " + year);
            return Optional.empty();
        }

        Enrollment en = new Enrollment();
        en.setStudentId(studentId);
        en.setGradeId(gradeId);
        en.setAcademicYear(year);
        en.setEnrollmentDate(LocalDate.now());
        en.setStatus("Activa");
        en.setObservations(notes);

        return Optional.ofNullable(academicDAO.enrollStudent(en));
    }

    public Optional<Enrollment> getStudentEnrollment(int studentId, int year) {
        return academicDAO.findEnrollmentByStudentAndYear(studentId, year);
    }

    public List<Enrollment> getEnrollmentsByGradeAndYear(int gradeId, int year) {
        return academicDAO.findEnrollmentsByGradeAndYear(gradeId, year);
    }

    // --- Course Registrations ---

    public Optional<CourseRegistration> registerStudentInCourse(int studentId, int courseId, int year, String period, String section) {
        CourseRegistration reg = new CourseRegistration();
        reg.setStudentId(studentId);
        reg.setCourseId(courseId);
        reg.setAcademicYear(year);
        reg.setAcademicPeriod(period);
        reg.setSection(section);
        reg.setRegistrationDate(LocalDate.now());
        reg.setStatus("Activa");

        return Optional.ofNullable(academicDAO.registerStudentInCourse(reg));
    }

    public List<CourseRegistration> getStudentRegistrations(int studentId, int year, String period) {
        return academicDAO.findRegistrationsByStudent(studentId, year, period);
    }

    // --- Grading & GPA Calculations (Using Java Streams) ---

    public Optional<AcademicRecord> recordGrade(int studentId, int courseId, int teacherId, int year,
                                               String period, double gradeValue, String observations) {
        if (gradeValue < 0.0 || gradeValue > 5.0) {
            AppLogger.warning("Invalid grade value: " + gradeValue + ". Must be within 0.0 and 5.0");
            return Optional.empty();
        }

        BigDecimal decimalGrade = BigDecimal.valueOf(gradeValue).setScale(1, RoundingMode.HALF_UP);
        AcademicRecord record = new AcademicRecord();
        record.setStudentId(studentId);
        record.setCourseId(courseId);
        record.setTeacherId(teacherId);
        record.setAcademicYear(year);
        record.setAcademicPeriod(period);
        record.setGradeValue(decimalGrade);
        record.setObservations(observations);
        record.setRecordDate(LocalDate.now());

        return Optional.ofNullable(academicDAO.recordGrade(record));
    }

    public List<AcademicRecord> getStudentGrades(int studentId) {
        return academicDAO.findGradesByStudent(studentId);
    }

    /**
     * Calculates the cumulative GPA for a student across all subjects using Stream API.
     *
     * @param studentId Student ID
     * @return Calculated GPA, or 0.0 if no grades found
     */
    public double calculateStudentCumulativeGpa(int studentId) {
        List<AcademicRecord> grades = academicDAO.findGradesByStudent(studentId);
        return grades.stream()
                .filter(g -> g.getGradeValue() != null)
                .mapToDouble(g -> g.getGradeValue().doubleValue())
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates the student's term GPA for a specific academic period using Stream API.
     */
    public double calculateStudentPeriodGpa(int studentId, int year, String period) {
        List<AcademicRecord> grades = academicDAO.findGradesByStudent(studentId);
        return grades.stream()
                .filter(g -> g.getAcademicYear() == year && period.equalsIgnoreCase(g.getAcademicPeriod()))
                .filter(g -> g.getGradeValue() != null)
                .mapToDouble(g -> g.getGradeValue().doubleValue())
                .average()
                .orElse(0.0);
    }

    /**
     * Calculates average grade per subject for a specific student using Stream groupingBy.
     */
    public Map<String, Double> calculateAveragePerSubjectForStudent(int studentId) {
        List<AcademicRecord> grades = academicDAO.findGradesByStudent(studentId);
        return grades.stream()
                .filter(g -> g.getGradeValue() != null)
                .collect(Collectors.groupingBy(
                        AcademicRecord::getCourseName,
                        Collectors.averagingDouble(g -> g.getGradeValue().doubleValue())
                ));
    }

    /**
     * Calculates the overall class average for a subject in a specific term.
     */
    public double calculateCourseAverage(int courseId, int year, String period) {
        List<AcademicRecord> grades = academicDAO.findGradesByCourseAndPeriod(courseId, year, period);
        return grades.stream()
                .filter(g -> g.getGradeValue() != null)
                .mapToDouble(g -> g.getGradeValue().doubleValue())
                .average()
                .orElse(0.0);
    }

    public List<AcademicRecord> getCourseGrades(int courseId, int year, String period) {
        return academicDAO.findGradesByCourseAndPeriod(courseId, year, period);
    }

    // --- Attendance ---

    public Optional<Attendance> markAttendance(int studentId, int courseId, LocalDate date, String status, String obs, Integer teacherId) {
        Attendance att = new Attendance();
        att.setStudentId(studentId);
        att.setCourseId(courseId);
        att.setAttendanceDate(date);
        att.setStatus(status);
        att.setObservations(obs);
        att.setRegisteredByTeacherId(teacherId);

        return Optional.ofNullable(academicDAO.recordAttendance(att));
    }

    public List<Attendance> getAttendance(int studentId, int courseId) {
        return academicDAO.findAttendanceByStudentAndCourse(studentId, courseId);
    }

    public List<Attendance> getStudentAbsences(int studentId) {
        return academicDAO.findAbsencesByStudent(studentId);
    }
}
