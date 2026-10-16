package com.littlehorizons.erp.model;

/**
 * Domain entity representing the assignment of a teacher to a course/section (Asignación Profesor).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class TeacherAssignment {

    private int id;
    private int teacherId;
    private int courseId;
    private int academicYear;
    private String section;

    // Relational join fields
    private String teacherName;
    private String courseName;
    private String courseCode;

    public TeacherAssignment() {
    }

    public TeacherAssignment(int id, int teacherId, int courseId, int academicYear, String section) {
        this.id = id;
        this.teacherId = teacherId;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.section = section;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getAcademicYear() { return academicYear; }
    public void setAcademicYear(int academicYear) { this.academicYear = academicYear; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    @Override
    public String toString() {
        return String.format("[%d] %s -> %s (Sec: %s, Year: %d)",
                id, teacherName != null ? teacherName : teacherId, courseName != null ? courseName : courseId, section, academicYear);
    }
}
