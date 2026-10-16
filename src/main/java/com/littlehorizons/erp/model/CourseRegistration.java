package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing a student's enrollment in a specific course for a term (Inscripción Materia).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class CourseRegistration {

    private int id;
    private int studentId;
    private int courseId;
    private int academicYear;
    private String academicPeriod; // 'I', 'II', 'III', 'IV'
    private String section;
    private LocalDate registrationDate;
    private String status; // 'Activa', 'Retirada', 'Completada'

    // Relational join fields
    private String studentName;
    private String courseName;
    private String courseCode;

    public CourseRegistration() {
    }

    public CourseRegistration(int id, int studentId, int courseId, int academicYear,
                              String academicPeriod, String section, LocalDate registrationDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.academicPeriod = academicPeriod;
        this.section = section;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(String academicPeriod) { this.academicPeriod = academicPeriod; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    @Override
    public String toString() {
        return String.format("[%d] Student: %s | Course: %s | Period: %s %d",
                id, studentName != null ? studentName : studentId, courseName != null ? courseName : courseId, academicPeriod, academicYear);
    }
}
