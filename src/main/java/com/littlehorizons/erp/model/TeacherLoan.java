package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing a resource loan issued to a teacher (Préstamo Profesor).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class TeacherLoan {

    private int id;
    private int resourceId;
    private int teacherId;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String status; // 'Activo', 'Devuelto', 'Vencido'
    private String observations;

    // Relational join fields
    private String resourceCode;
    private String resourceName;
    private String teacherName;
    private String teacherEmail;

    public TeacherLoan() {
    }

    public TeacherLoan(int id, int resourceId, int teacherId, LocalDate loanDate,
                       LocalDate expectedReturnDate, LocalDate actualReturnDate, String status, String observations) {
        this.id = id;
        this.resourceId = resourceId;
        this.teacherId = teacherId;
        this.loanDate = loanDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.observations = observations;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getResourceCode() { return resourceCode; }
    public void setResourceCode(String resourceCode) { this.resourceCode = resourceCode; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getTeacherEmail() { return teacherEmail; }
    public void setTeacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; }

    public boolean isOverdue() {
        if ("Devuelto".equalsIgnoreCase(status)) return false;
        return expectedReturnDate != null && expectedReturnDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) loaned to Teacher: %s | Due: %s | Status: %s",
                id, resourceName != null ? resourceName : resourceId, resourceCode != null ? resourceCode : "",
                teacherName != null ? teacherName : teacherId, expectedReturnDate, status);
    }
}
