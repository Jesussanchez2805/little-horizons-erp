package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Academic operations:
 * Grades, Courses, Enrollments, Subject Registrations, Evaluations and Attendance.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface AcademicDAO {
    // Grade Levels
    List<GradeLevel> findAllGradeLevels();
    Optional<GradeLevel> findGradeLevelById(int id);

    // Enrollments
    Enrollment enrollStudent(Enrollment enrollment);
    Optional<Enrollment> findEnrollmentByStudentAndYear(int studentId, int academicYear);
    List<Enrollment> findEnrollmentsByGradeAndYear(int gradeId, int academicYear);

    // Courses
    Course createCourse(Course course);
    Optional<Course> findCourseById(int courseId);
    Optional<Course> findCourseByCode(String code);
    List<Course> findAllCourses();
    List<Course> findCoursesByGrade(int gradeId);

    // Course Registrations
    CourseRegistration registerStudentInCourse(CourseRegistration registration);
    List<CourseRegistration> findRegistrationsByStudent(int studentId, int academicYear, String period);

    // Academic Records (Grades)
    AcademicRecord recordGrade(AcademicRecord record);
    List<AcademicRecord> findGradesByStudent(int studentId);
    List<AcademicRecord> findGradesByStudentAndCourse(int studentId, int courseId);
    List<AcademicRecord> findGradesByCourseAndPeriod(int courseId, int academicYear, String period);

    // Attendance
    Attendance recordAttendance(Attendance attendance);
    List<Attendance> findAttendanceByStudentAndCourse(int studentId, int courseId);
    List<Attendance> findAbsencesByStudent(int studentId);
}
