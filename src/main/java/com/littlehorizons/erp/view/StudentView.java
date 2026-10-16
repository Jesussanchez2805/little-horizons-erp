package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.StudentController;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Console view for the Student & Guardian Management module.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class StudentView {

    private final StudentController studentController;

    public StudentView(StudentController studentController) {
        this.studentController = studentController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MODULE 1: STUDENT & GUARDIAN MANAGEMENT", "Little Horizons Educational ERP");
            System.out.println(" 1. Register New Student");
            System.out.println(" 2. List All Students (Directory)");
            System.out.println(" 3. Search Student by ID / Document");
            System.out.println(" 4. Register Guardian (Acudiente)");
            System.out.println(" 5. Associate Guardian to Student");
            System.out.println(" 6. View Student Guardians & Emergency Contacts");
            System.out.println(" 7. Update Student Status");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 7);

            switch (choice) {
                case 1 -> registerStudent();
                case 2 -> listAllStudents();
                case 3 -> searchStudent();
                case 4 -> registerGuardian();
                case 5 -> linkGuardianToStudent();
                case 6 -> viewStudentGuardians();
                case 7 -> updateStudentStatus();
                case 0 -> back = true;
            }
        }
    }

    private void registerStudent() {
        ConsoleUtils.printSection("REGISTER NEW STUDENT");

        String docType = InputValidator.readNonEmptyString("Document Type (TI, CC, CE, Pasaporte)");
        String docNumber = InputValidator.readNonEmptyString("Document Number");
        String firstName = InputValidator.readNonEmptyString("First Name");
        String secondName = InputValidator.readOptionalString("Middle / Second Name", "");
        String firstLastName = InputValidator.readNonEmptyString("First Last Name");
        String secondLastName = InputValidator.readOptionalString("Second Last Name", "");
        LocalDate birthDate = InputValidator.readDate("Birth Date");
        String gender = InputValidator.readNonEmptyString("Gender (M, F, Otro)");
        String address = InputValidator.readNonEmptyString("Residential Address");
        String phone = InputValidator.readNonEmptyString("Contact Phone Number");
        String email = InputValidator.readEmail("Institutional / Contact Email");
        String bloodType = InputValidator.readNonEmptyString("Blood Type (e.g., O+, A+, B-)");
        String allergies = InputValidator.readOptionalString("Allergies", "Ninguna");
        String medicalConditions = InputValidator.readOptionalString("Medical Conditions", "Ninguna");

        Student student = new Student(0, docType, docNumber, firstName, secondName, firstLastName,
                secondLastName, birthDate, gender, address, phone, email, bloodType, allergies,
                medicalConditions, LocalDate.now(), "Activo");

        Optional<Student> result = studentController.registerStudent(student);
        if (result.isPresent()) {
            ConsoleUtils.printSuccess("Student registered successfully with ID: " + result.get().getId());
        } else {
            ConsoleUtils.printError("Failed to register student. The document number may already exist.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void listAllStudents() {
        ConsoleUtils.printSection("STUDENT DIRECTORY");
        List<Student> students = studentController.getAllStudents();

        List<String> headers = List.of("ID", "Doc Type", "Doc Number", "Full Name", "Gender", "Phone", "Status");
        List<List<String>> rows = new ArrayList<>();

        for (Student s : students) {
            rows.add(List.of(
                    String.valueOf(s.getId()),
                    s.getDocumentType(),
                    s.getDocumentNumber(),
                    s.getFullName(),
                    s.getGender(),
                    s.getPhone() != null ? s.getPhone() : "N/A",
                    s.getStatus()
            ));
        }

        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.printInfo("Total students registered: " + students.size());
        InputValidator.pressEnterToContinue();
    }

    private void searchStudent() {
        ConsoleUtils.printSection("SEARCH STUDENT");
        System.out.println("1. Search by Database ID");
        System.out.println("2. Search by Identity Document");
        int mode = InputValidator.readInt("Select search type", 1, 2);

        Optional<Student> studentOpt = Optional.empty();
        if (mode == 1) {
            int id = InputValidator.readInt("Enter Student ID", 1, 999999);
            studentOpt = studentController.getStudentById(id);
        } else {
            String docType = InputValidator.readNonEmptyString("Document Type (TI, CC, CE)");
            String docNumber = InputValidator.readNonEmptyString("Document Number");
            studentOpt = studentController.getStudentByDocument(docType, docNumber);
        }

        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            ConsoleUtils.printSuccess("Student found:");
            System.out.println("  • ID:                  " + s.getId());
            System.out.println("  • Full Name:           " + s.getFullName());
            System.out.println("  • Document:            " + s.getDocumentType() + " " + s.getDocumentNumber());
            System.out.println("  • Birth Date:          " + s.getBirthDate());
            System.out.println("  • Gender:              " + s.getGender());
            System.out.println("  • Email:               " + s.getEmail());
            System.out.println("  • Phone:               " + s.getPhone());
            System.out.println("  • Address:             " + s.getAddress());
            System.out.println("  • Blood Type:          " + s.getBloodType());
            System.out.println("  • Allergies:           " + s.getAllergies());
            System.out.println("  • Medical Conditions:  " + s.getMedicalConditions());
            System.out.println("  • Status:              " + s.getStatus());
        } else {
            ConsoleUtils.printError("Student not found.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerGuardian() {
        ConsoleUtils.printSection("REGISTER LEGAL GUARDIAN");

        String docType = InputValidator.readNonEmptyString("Document Type (CC, CE, Pasaporte)");
        String docNumber = InputValidator.readNonEmptyString("Document Number");
        String firstName = InputValidator.readNonEmptyString("First Name");
        String secondName = InputValidator.readOptionalString("Middle Name", "");
        String firstLastName = InputValidator.readNonEmptyString("First Last Name");
        String secondLastName = InputValidator.readOptionalString("Second Last Name", "");
        String relationship = InputValidator.readNonEmptyString("Relationship (Padre, Madre, Abuelo, Tutor Legal)");
        String primaryPhone = InputValidator.readNonEmptyString("Primary Phone");
        String secondaryPhone = InputValidator.readOptionalString("Secondary Phone", "");
        String email = InputValidator.readEmail("Email");
        String address = InputValidator.readNonEmptyString("Address");
        String occupation = InputValidator.readOptionalString("Occupation", "Independiente");
        boolean financial = InputValidator.readBoolean("Is financially responsible for payments?");

        Guardian g = new Guardian(0, docType, docNumber, firstName, secondName, firstLastName,
                secondLastName, relationship, primaryPhone, secondaryPhone, email, address, occupation, financial);

        Optional<Guardian> res = studentController.registerGuardian(g);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Guardian registered with ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Failed to register guardian.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void linkGuardianToStudent() {
        ConsoleUtils.printSection("LINK GUARDIAN TO STUDENT");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        int guardianId = InputValidator.readInt("Enter Guardian ID", 1, 999999);
        boolean isPrimary = InputValidator.readBoolean("Is primary contact for communications?");
        boolean isEmergency = InputValidator.readBoolean("Is emergency contact?");

        boolean ok = studentController.linkStudentToGuardian(studentId, guardianId, isPrimary, isEmergency);
        if (ok) {
            ConsoleUtils.printSuccess("Guardian successfully linked to student.");
        } else {
            ConsoleUtils.printError("Could not link guardian. Check if both IDs exist in database.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentGuardians() {
        ConsoleUtils.printSection("STUDENT GUARDIANS & EMERGENCY CONTACTS");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);

        List<Guardian> guardians = studentController.getGuardiansForStudent(studentId);
        if (guardians.isEmpty()) {
            ConsoleUtils.printWarning("No guardians associated with Student ID " + studentId);
        } else {
            List<String> headers = List.of("ID", "Full Name", "Relationship", "Primary Phone", "Email", "Financial Resp.");
            List<List<String>> rows = new ArrayList<>();
            for (Guardian g : guardians) {
                rows.add(List.of(
                        String.valueOf(g.getId()),
                        g.getFullName(),
                        g.getRelationship(),
                        g.getPrimaryPhone(),
                        g.getEmail(),
                        g.isFinancialResponsible() ? "YES" : "NO"
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void updateStudentStatus() {
        ConsoleUtils.printSection("UPDATE STUDENT STATUS");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);

        Optional<Student> opt = studentController.getStudentById(studentId);
        if (opt.isEmpty()) {
            ConsoleUtils.printError("Student not found.");
            InputValidator.pressEnterToContinue();
            return;
        }

        Student s = opt.get();
        System.out.println("Current status: " + s.getStatus());
        System.out.println("1. Activo");
        System.out.println("2. Graduado");
        System.out.println("3. Retirado");
        System.out.println("4. Suspendido");
        int ch = InputValidator.readInt("Select new status", 1, 4);
        String[] statuses = {"Activo", "Graduado", "Retirado", "Suspendido"};
        s.setStatus(statuses[ch - 1]);

        boolean updated = studentController.updateStudent(s);
        if (updated) {
            ConsoleUtils.printSuccess("Student status updated to: " + s.getStatus());
        } else {
            ConsoleUtils.printError("Failed to update status.");
        }
        InputValidator.pressEnterToContinue();
    }
}
