package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.ResourceController;
import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.ResourceItem;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Console view for the Resource Management module:
 * Library, Science Laboratories, Sports, Student Loans, Teacher Loans, and Overdue Returns.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ResourceView {

    private final ResourceController resourceController;

    public ResourceView(ResourceController resourceController) {
        this.resourceController = resourceController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MODULE 6: RESOURCE MANAGEMENT (LIBRARY & LABS)", "Little Horizons Educational ERP");
            System.out.println(" 1. Browse Resource Inventory (Catálogo de Recursos)");
            System.out.println(" 2. Register New Resource in Inventory");
            System.out.println(" 3. Issue Loan to Student (Préstamo a Estudiante)");
            System.out.println(" 4. Process Return from Student (Devolución)");
            System.out.println(" 5. Issue Loan to Teacher (Préstamo a Profesor)");
            System.out.println(" 6. Process Return from Teacher (Devolución)");
            System.out.println(" 7. View Active Loans (Student & Faculty)");
            System.out.println(" 8. Overdue Loans Report (Préstamos Vencidos)");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 8);

            switch (choice) {
                case 1 -> browseInventory();
                case 2 -> registerResource();
                case 3 -> issueStudentLoan();
                case 4 -> returnStudentLoan();
                case 5 -> issueTeacherLoan();
                case 6 -> returnTeacherLoan();
                case 7 -> viewActiveLoans();
                case 8 -> viewOverdueLoans();
                case 0 -> back = true;
            }
        }
    }

    private void browseInventory() {
        ConsoleUtils.printSection("RESOURCE INVENTORY");
        List<ResourceItem> items = resourceController.getAllResources();

        List<String> headers = List.of("ID", "Code", "Resource Name", "Category", "Location", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (ResourceItem r : items) {
            rows.add(List.of(
                    String.valueOf(r.getId()),
                    r.getCode(),
                    r.getName(),
                    r.getCategoryName() != null ? r.getCategoryName() : String.valueOf(r.getCategoryId()),
                    r.getPhysicalLocation() != null ? r.getPhysicalLocation() : "General",
                    r.getStatus()
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        ConsoleUtils.printInfo("Total resources in catalog: " + items.size());
        InputValidator.pressEnterToContinue();
    }

    private void registerResource() {
        ConsoleUtils.printSection("REGISTER INVENTORY RESOURCE");
        String code = InputValidator.readNonEmptyString("Resource Unique Code (e.g., LIB-011, LAB-009)");
        String name = InputValidator.readNonEmptyString("Resource Name");

        List<ResourceCategory> cats = resourceController.getAllCategories();
        System.out.println("\nSelect Category:");
        for (ResourceCategory c : cats) {
            System.out.printf("  [%d] %s%n", c.getId(), c.getName());
        }
        int catId = InputValidator.readInt("Category ID", 1, 5);
        String desc = InputValidator.readNonEmptyString("Description");
        String location = InputValidator.readNonEmptyString("Physical Storage Location (e.g. Biblioteca - Estante C)");

        ResourceItem item = new ResourceItem(0, code, name, catId, desc, LocalDate.now(), location, "Disponible");
        Optional<ResourceItem> res = resourceController.createResource(item);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Resource added to catalog with ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Failed to register resource. Code might already exist.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void issueStudentLoan() {
        ConsoleUtils.printSection("ISSUE RESOURCE LOAN TO STUDENT");
        int resourceId = InputValidator.readInt("Enter Resource ID", 1, 999999);
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        LocalDate returnDate = InputValidator.readDate("Expected Return Date");
        String obs = InputValidator.readOptionalString("Loan Observations", "Préstamo de biblioteca/laboratorio");

        Optional<StudentLoan> loan = resourceController.loanToStudent(resourceId, studentId, returnDate, obs);
        if (loan.isPresent()) {
            ConsoleUtils.printSuccess("Loan issued successfully! Loan ID: " + loan.get().getId());
            ConsoleUtils.printInfo("Resource status updated automatically to 'Prestado'.");
        } else {
            ConsoleUtils.printError("Could not issue loan. Ensure resource exists and is currently 'Disponible'.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void returnStudentLoan() {
        ConsoleUtils.printSection("RETURN STUDENT LOAN");
        int loanId = InputValidator.readInt("Enter Student Loan ID", 1, 999999);
        String obs = InputValidator.readOptionalString("Return Condition Remarks", "Devuelto en buen estado");

        boolean ok = resourceController.returnStudentLoan(loanId, obs);
        if (ok) {
            ConsoleUtils.printSuccess("Loan marked as 'Devuelto'. Resource status set to 'Disponible'.");
        } else {
            ConsoleUtils.printError("Failed to process loan return.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void issueTeacherLoan() {
        ConsoleUtils.printSection("ISSUE RESOURCE LOAN TO TEACHER");
        int resourceId = InputValidator.readInt("Enter Resource ID", 1, 999999);
        int teacherId = InputValidator.readInt("Enter Teacher ID", 1, 999999);
        LocalDate returnDate = InputValidator.readDate("Expected Return Date");
        String obs = InputValidator.readOptionalString("Loan Observations", "Uso docente en clase");

        Optional<TeacherLoan> loan = resourceController.loanToTeacher(resourceId, teacherId, returnDate, obs);
        if (loan.isPresent()) {
            ConsoleUtils.printSuccess("Teacher loan issued successfully! Loan ID: " + loan.get().getId());
            ConsoleUtils.printInfo("Resource status updated automatically to 'Prestado'.");
        } else {
            ConsoleUtils.printError("Could not issue loan. Ensure resource exists and is currently 'Disponible'.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void returnTeacherLoan() {
        ConsoleUtils.printSection("RETURN TEACHER LOAN");
        int loanId = InputValidator.readInt("Enter Teacher Loan ID", 1, 999999);
        String obs = InputValidator.readOptionalString("Return Condition Remarks", "Devuelto en buen estado");

        boolean ok = resourceController.returnTeacherLoan(loanId, obs);
        if (ok) {
            ConsoleUtils.printSuccess("Teacher loan marked as 'Devuelto'. Resource status set to 'Disponible'.");
        } else {
            ConsoleUtils.printError("Failed to process loan return.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewActiveLoans() {
        ConsoleUtils.printSection("ACTIVE STUDENT LOANS");
        List<StudentLoan> sLoans = resourceController.getActiveStudentLoans();
        List<String> sHeaders = List.of("Loan ID", "Resource Code", "Resource Name", "Student", "Due Date", "Status");
        List<List<String>> sRows = new ArrayList<>();
        for (StudentLoan sl : sLoans) {
            sRows.add(List.of(
                    String.valueOf(sl.getId()),
                    sl.getResourceCode(),
                    sl.getResourceName(),
                    sl.getStudentName(),
                    sl.getExpectedReturnDate().toString(),
                    sl.getStatus()
            ));
        }
        ConsoleUtils.printTable(sHeaders, sRows);

        ConsoleUtils.printSection("ACTIVE TEACHER LOANS");
        List<TeacherLoan> tLoans = resourceController.getActiveTeacherLoans();
        List<String> tHeaders = List.of("Loan ID", "Resource Code", "Resource Name", "Teacher", "Due Date", "Status");
        List<List<String>> tRows = new ArrayList<>();
        for (TeacherLoan tl : tLoans) {
            tRows.add(List.of(
                    String.valueOf(tl.getId()),
                    tl.getResourceCode(),
                    tl.getResourceName(),
                    tl.getTeacherName(),
                    tl.getExpectedReturnDate().toString(),
                    tl.getStatus()
            ));
        }
        ConsoleUtils.printTable(tHeaders, tRows);
        InputValidator.pressEnterToContinue();
    }

    private void viewOverdueLoans() {
        ConsoleUtils.printSection("OVERDUE LOANS AUDIT REPORT (PRÉSTAMOS VENCIDOS)");
        List<StudentLoan> sOverdue = resourceController.getOverdueStudentLoans();
        List<TeacherLoan> tOverdue = resourceController.getOverdueTeacherLoans();

        if (sOverdue.isEmpty() && tOverdue.isEmpty()) {
            ConsoleUtils.printSuccess("Excellent! No loans are currently overdue.");
        } else {
            if (!sOverdue.isEmpty()) {
                System.out.println("\nOverdue Student Loans:");
                List<String> sHeaders = List.of("Loan ID", "Resource", "Student Name", "Expected Return Date");
                List<List<String>> sRows = new ArrayList<>();
                for (StudentLoan sl : sOverdue) {
                    sRows.add(List.of(
                            String.valueOf(sl.getId()),
                            sl.getResourceName(),
                            sl.getStudentName(),
                            sl.getExpectedReturnDate().toString()
                    ));
                }
                ConsoleUtils.printTable(sHeaders, sRows);
            }

            if (!tOverdue.isEmpty()) {
                System.out.println("\nOverdue Teacher Loans:");
                List<String> tHeaders = List.of("Loan ID", "Resource", "Teacher Name", "Expected Return Date");
                List<List<String>> tRows = new ArrayList<>();
                for (TeacherLoan tl : tOverdue) {
                    tRows.add(List.of(
                            String.valueOf(tl.getId()),
                            tl.getResourceName(),
                            tl.getTeacherName(),
                            tl.getExpectedReturnDate().toString()
                    ));
                }
                ConsoleUtils.printTable(tHeaders, tRows);
            }
        }
        InputValidator.pressEnterToContinue();
    }
}
