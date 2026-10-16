package com.littlehorizons.erp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain entity representing an academic grade evaluation (Registro Académico / Calificación).
 * Grade value is calibrated to the Colombian scale (0.0 to 5.0).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class AcademicRecord {

    private int id;
    private int studentId;
    private int courseId;
    private int teacherId;
    private int academicYear;
    private String academicPeriod; // 'I', 'II', 'III', 'IV', 'Final'
    private BigDecimal gradeValue; // Range: 0.0 to 5.0
    private String observations;
    private LocalDate recordDate;

    // Relational join fields
    private String studentName;
    private String studentDocNumber;
    private String courseName;
    private String courseCode;
    private String teacherName;

    public AcademicRecord() {
    }

    public AcademicRecord(int id, int studentId, int courseId, int teacherId, int academicYear,
                          String academicPeriod, BigDecimal gradeValue, String observations, LocalDate recordDate) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.academicYear = academicYear;
        this.academicPeriod = academicPeriod;
        this.gradeValue = gradeValue;
        this.observations = observations;
        this.recordDate = recordDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getAcademicPeriod() { return academicPeriod; }
    public void setAcademicPeriod(String academicPeriod) { this.academicPeriod = academicPeriod; }

    public BigDecimal getGradeValue() { return gradeValue; }
    public void setGradeValue(BigDecimal gradeValue) { this.gradeValue = gradeValue; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentDocNumber() { return studentDocNumber; }
    public void setStudentDocNumber(String studentDocNumber) { this.studentDocNumber = studentDocNumber; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    @Override
    public String toString() {
        return String.format("[%s - %s] %s: %.1f (%s)",
                courseCode != null ? courseCode : courseId, academicPeriod, studentName != null ? studentName : studentId, gradeValue != null ? gradeValue.doubleValue() : 0.0, observations != null ? observations : "");
    }
}
