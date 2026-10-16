package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.AcademicController;
import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Console view for the Academic Management module:
 * Matrículas, Cursos, Calificaciones (Notas 0.0-5.0), Cálculo de Promedios, y Asistencia.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class AcademicView {

    private final AcademicController academicController;

    public AcademicView(AcademicController academicController) {
        this.academicController = academicController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MODULE 2: ACADEMIC & GRADE MANAGEMENT", "Little Horizons Educational ERP");
            System.out.println(" 1. View Academic Grades & Course Catalog");
            System.out.println(" 2. Enroll Student in Academic Grade (Matrícula)");
            System.out.println(" 3. Register Student in Subject Course");
            System.out.println(" 4. Record Student Grade (Nota 0.0 - 5.0)");
            System.out.println(" 5. Consult Student Academic History & Cumulative GPA");
            System.out.println(" 6. Calculate Course Class Average (Promedio de Curso)");
            System.out.println(" 7. Record Daily Attendance (Presente, Ausente, Tardanza, Excusa)");
            System.out.println(" 8. View Student Attendance & Absence Patterns");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 8);

            switch (choice) {
                case 1 -> viewCoursesCatalog();
                case 2 -> enrollStudentInGrade();
                case 3 -> registerStudentInCourse();
                case 4 -> recordGrade();
                case 5 -> consultStudentHistoryAndGpa();
                case 6 -> calculateCourseAverage();
                case 7 -> recordAttendance();
                case 8 -> viewStudentAbsences();
                case 0 -> back = true;
            }
        }
    }

    private void viewCoursesCatalog() {
        ConsoleUtils.printSection("ACADEMIC COURSES CATALOG");
        List<Course> courses = academicController.getAllCourses();

        List<String> headers = List.of("ID", "Code", "Course Name", "Grade Level", "Hrs/Wk", "Active");
        List<List<String>> rows = new ArrayList<>();
        for (Course c : courses) {
            rows.add(List.of(
                    String.valueOf(c.getId()),
                    c.getCode(),
                    c.getName(),
                    c.getGradeName() != null ? c.getGradeName() : String.valueOf(c.getGradeId()),
                    String.valueOf(c.getWeeklyHours()),
                    c.isActive() ? "YES" : "NO"
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void enrollStudentInGrade() {
        ConsoleUtils.printSection("ENROLL STUDENT IN ACADEMIC GRADE");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);

        // Display grades
        List<GradeLevel> grades = academicController.getAllGradeLevels();
        System.out.println("\nAvailable Grade Levels:");
        for (GradeLevel gl : grades) {
            System.out.printf("  [%2d] %s (Level %d)%n", gl.getId(), gl.getName(), gl.getNumericLevel());
        }

        int gradeId = InputValidator.readInt("Select Grade Level ID", 1, 12);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String obs = InputValidator.readOptionalString("Enrollment Remarks", "Matrícula regular");

        Optional<Enrollment> result = academicController.enrollStudent(studentId, gradeId, year, obs);
        if (result.isPresent()) {
            ConsoleUtils.printSuccess("Student successfully enrolled in grade with enrollment ID: " + result.get().getId());
        } else {
            ConsoleUtils.printError("Could not enroll student. They may already be enrolled for this academic year.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerStudentInCourse() {
        ConsoleUtils.printSection("REGISTER STUDENT IN SUBJECT COURSE");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        int courseId = InputValidator.readInt("Enter Course ID", 1, 999999);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Academic Period (I, II, III, IV)");
        String section = InputValidator.readOptionalString("Class Section", "A");

        Optional<CourseRegistration> reg = academicController.registerStudentInCourse(studentId, courseId, year, period, section);
        if (reg.isPresent()) {
            ConsoleUtils.printSuccess("Course registration confirmed with ID: " + reg.get().getId());
        } else {
            ConsoleUtils.printError("Failed to register in course.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordGrade() {
        ConsoleUtils.printSection("RECORD STUDENT GRADE (CALIFICACIÓN)");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        int courseId = InputValidator.readInt("Enter Course ID", 1, 999999);
        int teacherId = InputValidator.readInt("Enter Teacher ID", 1, 999999);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Academic Period (I, II, III, IV, Final)");
        double grade = InputValidator.readDouble("Grade value (0.0 to 5.0)", 0.0, 5.0);
        String obs = InputValidator.readOptionalString("Evaluation Observations", "Desempeño satisfactorio");

        Optional<AcademicRecord> rec = academicController.recordGrade(studentId, courseId, teacherId, year, period, grade, obs);
        if (rec.isPresent()) {
            ConsoleUtils.printSuccess(String.format("Grade recorded: %.1f for Student %d in Course %d", grade, studentId, courseId));
        } else {
            ConsoleUtils.printError("Failed to record grade.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void consultStudentHistoryAndGpa() {
        ConsoleUtils.printSection("STUDENT ACADEMIC TRANSCRIPT & GPA");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);

        List<AcademicRecord> grades = academicController.getStudentGrades(studentId);
        if (grades.isEmpty()) {
            ConsoleUtils.printWarning("No grade records found for Student ID " + studentId);
        } else {
            List<String> headers = List.of("Year", "Period", "Course Code", "Course Name", "Teacher", "Grade", "Observations");
            List<List<String>> rows = new ArrayList<>();
            for (AcademicRecord r : grades) {
                rows.add(List.of(
                        String.valueOf(r.getAcademicYear()),
                        r.getAcademicPeriod(),
                        r.getCourseCode() != null ? r.getCourseCode() : "",
                        r.getCourseName() != null ? r.getCourseName() : "",
                        r.getTeacherName() != null ? r.getTeacherName() : "",
                        String.format("%.1f", r.getGradeValue().doubleValue()),
                        r.getObservations() != null ? r.getObservations() : ""
                ));
            }
            ConsoleUtils.printTable(headers, rows);

            // Compute cumulative GPA and per-subject breakdown using Stream API
            double cumulativeGpa = academicController.calculateStudentCumulativeGpa(studentId);
            System.out.println();
            ConsoleUtils.printSuccess(String.format(">> Cumulative Grade Point Average (GPA): %.2f / 5.0 <<", cumulativeGpa));

            System.out.println("\nAverage by Subject:");
            Map<String, Double> bySubject = academicController.calculateAveragePerSubjectForStudent(studentId);
            bySubject.forEach((course, avg) -> System.out.printf("  • %-35s : %.2f / 5.0%n", course, avg));
        }
        InputValidator.pressEnterToContinue();
    }

    private void calculateCourseAverage() {
        ConsoleUtils.printSection("CALCULATE COURSE CLASS AVERAGE");
        int courseId = InputValidator.readInt("Enter Course ID", 1, 999999);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Academic Period (I, II, III, IV, Final)");

        List<AcademicRecord> records = academicController.getCourseGrades(courseId, year, period);
        if (records.isEmpty()) {
            ConsoleUtils.printWarning("No grade records for Course " + courseId + " in " + year + " " + period);
        } else {
            List<String> headers = List.of("Student Name", "Document", "Grade", "Observations");
            List<List<String>> rows = new ArrayList<>();
            for (AcademicRecord r : records) {
                rows.add(List.of(
                        r.getStudentName(),
                        r.getStudentDocNumber(),
                        String.format("%.1f", r.getGradeValue().doubleValue()),
                        r.getObservations() != null ? r.getObservations() : ""
                ));
            }
            ConsoleUtils.printTable(headers, rows);

            double courseAvg = academicController.calculateCourseAverage(courseId, year, period);
            ConsoleUtils.printSuccess(String.format("Overall Course Class Average for Course %d: %.2f / 5.0 (Total: %d students)",
                    courseId, courseAvg, records.size()));
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordAttendance() {
        ConsoleUtils.printSection("RECORD DAILY ATTENDANCE");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        int courseId = InputValidator.readInt("Enter Course ID", 1, 999999);
        LocalDate date = InputValidator.readDate("Attendance Date");

        System.out.println("Select Status:");
        System.out.println("1. Presente");
        System.out.println("2. Ausente");
        System.out.println("3. Tardanza");
        System.out.println("4. Excusa Médica");
        int st = InputValidator.readInt("Option", 1, 4);
        String[] statuses = {"Presente", "Ausente", "Tardanza", "Excusa Médica"};
        String status = statuses[st - 1];

        String obs = InputValidator.readOptionalString("Remarks", "");
        int teacherId = InputValidator.readInt("Registered by Teacher ID", 1, 999999);

        Optional<Attendance> att = academicController.markAttendance(studentId, courseId, date, status, obs, teacherId);
        if (att.isPresent()) {
            ConsoleUtils.printSuccess("Attendance recorded: " + status + " on " + date);
        } else {
            ConsoleUtils.printError("Failed to record attendance.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentAbsences() {
        ConsoleUtils.printSection("STUDENT ATTENDANCE & ABSENCES REPORT");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);

        List<Attendance> absences = academicController.getStudentAbsences(studentId);
        if (absences.isEmpty()) {
            ConsoleUtils.printSuccess("Student has excellent attendance. No absences recorded.");
        } else {
            List<String> headers = List.of("Date", "Course", "Status", "Remarks");
            List<List<String>> rows = new ArrayList<>();
            for (Attendance a : absences) {
                rows.add(List.of(
                        a.getAttendanceDate().toString(),
                        a.getCourseName() != null ? a.getCourseName() : String.valueOf(a.getCourseId()),
                        a.getStatus(),
                        a.getObservations() != null ? a.getObservations() : ""
                ));
            }
            ConsoleUtils.printTable(headers, rows);
            if (absences.size() >= 3) {
                ConsoleUtils.printWarning(String.format("CONCERN: Student has accumulated %d absences/tardinesses. Parental follow-up advised.", absences.size()));
            }
        }
        InputValidator.pressEnterToContinue();
    }
}
