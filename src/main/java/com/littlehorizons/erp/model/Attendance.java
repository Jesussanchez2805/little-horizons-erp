package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing daily student attendance for a course (Asistencia).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class Attendance {

    private int id;
    private int studentId;
    private int courseId;
    private LocalDate attendanceDate;
    private String status; // 'Presente', 'Ausente', 'Tardanza', 'Excusa Médica'
    private String observations;
    private Integer registeredByTeacherId;

    // Relational join fields
    private String studentName;
    private String courseName;
    private String registeredByTeacherName;

    public Attendance() {
    }

    public Attendance(int id, int studentId, int courseId, LocalDate attendanceDate,
                      String status, String observations, Integer registeredByTeacherId) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.observations = observations;
        this.registeredByTeacherId = registeredByTeacherId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public Integer getRegisteredByTeacherId() { return registeredByTeacherId; }
    public void setRegisteredByTeacherId(Integer registeredByTeacherId) { this.registeredByTeacherId = registeredByTeacherId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getRegisteredByTeacherName() { return registeredByTeacherName; }
    public void setRegisteredByTeacherName(String registeredByTeacherName) { this.registeredByTeacherName = registeredByTeacherName; }

    @Override
    public String toString() {
        return String.format("[%s] Student: %s | Course: %s | Status: %s",
                attendanceDate, studentName != null ? studentName : studentId, courseName != null ? courseName : courseId, status);
    }
}
