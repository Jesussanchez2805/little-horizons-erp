package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing an annual student enrollment (Matrícula).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class Enrollment {

    private int id;
    private int studentId;
    private int gradeId;
    private int academicYear;
    private String status; // 'Activa', 'Completada', 'Retirada'
    private LocalDate enrollmentDate;
    private LocalDate withdrawalDate;
    private String observations;

    // Relational join fields
    private String studentName;
    private String studentDocNumber;
    private String gradeName;

    public Enrollment() {
    }

    public Enrollment(int id, int studentId, int gradeId, int academicYear, String status,
                      LocalDate enrollmentDate, LocalDate withdrawalDate, String observations) {
        this.id = id;
        this.studentId = studentId;
        this.gradeId = gradeId;
        this.academicYear = academicYear;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
        this.withdrawalDate = withdrawalDate;
        this.observations = observations;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public LocalDate getWithdrawalDate() { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate withdrawalDate) { this.withdrawalDate = withdrawalDate; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentDocNumber() { return studentDocNumber; }
    public void setStudentDocNumber(String studentDocNumber) { this.studentDocNumber = studentDocNumber; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    @Override
    public String toString() {
        return String.format("[%d] Student: %s | Grade: %s | Year: %d | Status: %s",
                id, studentName != null ? studentName : studentId, gradeName != null ? gradeName : gradeId, academicYear, status);
    }
}
