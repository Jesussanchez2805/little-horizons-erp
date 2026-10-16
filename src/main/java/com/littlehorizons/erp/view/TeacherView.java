package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.TeacherController;
import com.littlehorizons.erp.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Console view for the Teacher Management & Course Assignment module.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class TeacherView {

    private final TeacherController teacherController;

    public TeacherView(TeacherController teacherController) {
        this.teacherController = teacherController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MODULE 3: TEACHER MANAGEMENT", "Little Horizons Educational ERP");
            System.out.println(" 1. List Faculty Directory");
            System.out.println(" 2. Register New Teacher");
            System.out.println(" 3. Search Teacher by ID");
            System.out.println(" 4. Assign Course to Teacher (Carga Académica)");
            System.out.println(" 5. View Teacher Workload & Assigned Courses");
            System.out.println(" 6. View Course Assigned Teachers");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 6);

            switch (choice) {
                case 1 -> listFacultyDirectory();
                case 2 -> registerTeacher();
                case 3 -> searchTeacher();
                case 4 -> assignCourseToTeacher();
                case 5 -> viewTeacherWorkload();
                case 6 -> viewCourseAssignments();
                case 0 -> back = true;
            }
        }
    }

    private void listFacultyDirectory() {
        ConsoleUtils.printSection("FACULTY DIRECTORY");
        List<Teacher> teachers = teacherController.getAllTeachers();

        List<String> headers = List.of("ID", "Doc Number", "Full Name", "Specialization", "Email", "Phone", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (Teacher t : teachers) {
            rows.add(List.of(
                    String.valueOf(t.getId()),
                    t.getDocumentNumber(),
                    t.getFullName(),
                    t.getSpecialization() != null ? t.getSpecialization() : "General",
                    t.getEmail(),
                    t.getPhone() != null ? t.getPhone() : "N/A",
                    t.getStatus()
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.printInfo("Total faculty members: " + teachers.size());
        InputValidator.pressEnterToContinue();
    }

    private void registerTeacher() {
        ConsoleUtils.printSection("REGISTER NEW TEACHER");

        String docType = InputValidator.readNonEmptyString("Document Type (CC, CE, Pasaporte)");
        String docNumber = InputValidator.readNonEmptyString("Document Number");
        String firstName = InputValidator.readNonEmptyString("First Name");
        String secondName = InputValidator.readOptionalString("Middle Name", "");
        String firstLastName = InputValidator.readNonEmptyString("First Last Name");
        String secondLastName = InputValidator.readOptionalString("Second Last Name", "");
        String email = InputValidator.readEmail("Email");
        String phone = InputValidator.readNonEmptyString("Phone");
        String spec = InputValidator.readNonEmptyString("Specialization / Department");
        LocalDate hire = InputValidator.readDate("Hire Date");

        Teacher t = new Teacher(0, docType, docNumber, firstName, secondName, firstLastName,
                secondLastName, email, phone, spec, hire, "Activo");

        Optional<Teacher> res = teacherController.registerTeacher(t);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Teacher registered successfully with ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Failed to register teacher. Check for duplicated document or email.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void searchTeacher() {
        ConsoleUtils.printSection("SEARCH TEACHER");
        int id = InputValidator.readInt("Enter Teacher ID", 1, 999999);

        Optional<Teacher> opt = teacherController.getTeacherById(id);
        if (opt.isPresent()) {
            Teacher t = opt.get();
            ConsoleUtils.printSuccess("Teacher details:");
            System.out.println("  • ID:             " + t.getId());
            System.out.println("  • Name:           " + t.getFullName());
            System.out.println("  • Document:       " + t.getDocumentType() + " " + t.getDocumentNumber());
            System.out.println("  • Specialization: " + t.getSpecialization());
            System.out.println("  • Email:          " + t.getEmail());
            System.out.println("  • Phone:          " + t.getPhone());
            System.out.println("  • Hire Date:      " + t.getHireDate());
            System.out.println("  • Status:         " + t.getStatus());
        } else {
            ConsoleUtils.printError("Teacher not found.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void assignCourseToTeacher() {
        ConsoleUtils.printSection("ASSIGN COURSE TO TEACHER");
        int teacherId = InputValidator.readInt("Enter Teacher ID", 1, 999999);
        int courseId = InputValidator.readInt("Enter Course ID", 1, 999999);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);
        String section = InputValidator.readOptionalString("Section / Group", "A");

        Optional<TeacherAssignment> res = teacherController.assignCourse(teacherId, courseId, year, section);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Course assigned successfully. Assignment ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Could not assign course. Check if teacher or course exists.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewTeacherWorkload() {
        ConsoleUtils.printSection("TEACHER WORKLOAD & ASSIGNMENTS");
        int teacherId = InputValidator.readInt("Enter Teacher ID", 1, 999999);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);

        List<TeacherAssignment> list = teacherController.getTeacherAssignments(teacherId, year);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No assignments found for Teacher ID " + teacherId + " in " + year);
        } else {
            List<String> headers = List.of("Assignment ID", "Course Code", "Course Name", "Section", "Year");
            List<List<String>> rows = new ArrayList<>();
            for (TeacherAssignment ta : list) {
                rows.add(List.of(
                        String.valueOf(ta.getId()),
                        ta.getCourseCode() != null ? ta.getCourseCode() : "",
                        ta.getCourseName() != null ? ta.getCourseName() : String.valueOf(ta.getCourseId()),
                        ta.getSection(),
                        String.valueOf(ta.getAcademicYear())
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewCourseAssignments() {
        ConsoleUtils.printSection("COURSE ASSIGNED TEACHERS");
        int courseId = InputValidator.readInt("Enter Course ID", 1, 999999);
        int year = InputValidator.readInt("Academic Year", 2020, 2035);

        List<TeacherAssignment> list = teacherController.getCourseAssignments(courseId, year);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No assigned teachers found for Course ID " + courseId + " in " + year);
        } else {
            List<String> headers = List.of("Assignment ID", "Teacher Name", "Section", "Year");
            List<List<String>> rows = new ArrayList<>();
            for (TeacherAssignment ta : list) {
                rows.add(List.of(
                        String.valueOf(ta.getId()),
                        ta.getTeacherName() != null ? ta.getTeacherName() : String.valueOf(ta.getTeacherId()),
                        ta.getSection(),
                        String.valueOf(ta.getAcademicYear())
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }
}
