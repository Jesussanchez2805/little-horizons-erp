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
            ConsoleUtils.printHeader("MÓDULO 6: GESTIÓN DE RECURSOS (BIBLIOTECA Y LABORATORIOS)", "Little Horizons Educational ERP");
            System.out.println(" 1. Explorar Inventario de Recursos");
            System.out.println(" 2. Registrar Nuevo Recurso en Inventario");
            System.out.println(" 3. Prestar Recurso a Estudiante");
            System.out.println(" 4. Procesar Devolución de Estudiante");
            System.out.println(" 5. Prestar Recurso a Profesor");
            System.out.println(" 6. Procesar Devolución de Profesor");
            System.out.println(" 7. Ver Préstamos Activos (Estudiantes y Profesores)");
            System.out.println(" 8. Reporte de Préstamos Vencidos en Mora");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 8);

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
        ConsoleUtils.printSection("INVENTARIO DE RECURSOS");
        List<ResourceItem> items = resourceController.getAllResources();

        List<String> headers = List.of("ID", "Código", "Nombre del Recurso", "Categoría", "Ubicación", "Estado");
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
        ConsoleUtils.printInfo("Total de recursos en catálogo: " + items.size());
        InputValidator.pressEnterToContinue();
    }

    private void registerResource() {
        ConsoleUtils.printSection("REGISTRAR RECURSO EN INVENTARIO");
        String code = InputValidator.readNonEmptyString("Código Único del Recurso (ej. LIB-011, LAB-009)");
        String name = InputValidator.readNonEmptyString("Nombre del Recurso");

        List<ResourceCategory> cats = resourceController.getAllCategories();
        System.out.println("\nSeleccione Categoría:");
        for (ResourceCategory c : cats) {
            System.out.printf("  [%d] %s%n", c.getId(), c.getName());
        }
        int catId = InputValidator.readInt("ID de la Categoría", 1, 5);
        String desc = InputValidator.readNonEmptyString("Descripción");
        String location = InputValidator.readNonEmptyString("Ubicación Física de Almacenamiento (ej. Biblioteca - Estante C)");

        ResourceItem item = new ResourceItem(0, code, name, catId, desc, LocalDate.now(), location, "Disponible");
        Optional<ResourceItem> res = resourceController.createResource(item);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Recurso añadido al catálogo con ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Error al registrar recurso. El código ya podría existir.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void issueStudentLoan() {
        ConsoleUtils.printSection("PRESTAR RECURSO A ESTUDIANTE");
        int resourceId = InputValidator.readInt("Ingrese ID del Recurso", 1, 999999);
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        LocalDate returnDate = InputValidator.readDate("Fecha Prevista de Devolución");
        String obs = InputValidator.readOptionalString("Observaciones del Préstamo", "Préstamo de biblioteca/laboratorio");

        Optional<StudentLoan> loan = resourceController.loanToStudent(resourceId, studentId, returnDate, obs);
        if (loan.isPresent()) {
            ConsoleUtils.printSuccess("¡Préstamo registrado exitosamente! ID de Préstamo: " + loan.get().getId());
            ConsoleUtils.printInfo("El estado del recurso se actualizó automáticamente a 'Prestado'.");
        } else {
            ConsoleUtils.printError("No se pudo realizar el préstamo. Asegúrese de que el recurso exista y esté 'Disponible'.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void returnStudentLoan() {
        ConsoleUtils.printSection("DEVOLUCIÓN DE PRÉSTAMO DE ESTUDIANTE");
        int loanId = InputValidator.readInt("Ingrese ID del Préstamo del Estudiante", 1, 999999);
        String obs = InputValidator.readOptionalString("Observaciones del Estado de Devolución", "Devuelto en buen estado");

        boolean ok = resourceController.returnStudentLoan(loanId, obs);
        if (ok) {
            ConsoleUtils.printSuccess("Préstamo marcado como 'Devuelto'. Estado del recurso restablecido a 'Disponible'.");
        } else {
            ConsoleUtils.printError("Error al procesar la devolución del préstamo.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void issueTeacherLoan() {
        ConsoleUtils.printSection("PRESTAR RECURSO A PROFESOR");
        int resourceId = InputValidator.readInt("Ingrese ID del Recurso", 1, 999999);
        int teacherId = InputValidator.readInt("Ingrese ID del Profesor", 1, 999999);
        LocalDate returnDate = InputValidator.readDate("Fecha Prevista de Devolución");
        String obs = InputValidator.readOptionalString("Observaciones del Préstamo", "Uso docente en clase");

        Optional<TeacherLoan> loan = resourceController.loanToTeacher(resourceId, teacherId, returnDate, obs);
        if (loan.isPresent()) {
            ConsoleUtils.printSuccess("¡Préstamo a profesor registrado exitosamente! ID de Préstamo: " + loan.get().getId());
            ConsoleUtils.printInfo("El estado del recurso se actualizó automáticamente a 'Prestado'.");
        } else {
            ConsoleUtils.printError("No se pudo realizar el préstamo. Asegúrese de que el recurso exista y esté 'Disponible'.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void returnTeacherLoan() {
        ConsoleUtils.printSection("DEVOLUCIÓN DE PRÉSTAMO DE PROFESOR");
        int loanId = InputValidator.readInt("Ingrese ID del Préstamo del Profesor", 1, 999999);
        String obs = InputValidator.readOptionalString("Observaciones del Estado de Devolución", "Devuelto en buen estado");

        boolean ok = resourceController.returnTeacherLoan(loanId, obs);
        if (ok) {
            ConsoleUtils.printSuccess("Préstamo marcado como 'Devuelto'. Estado del recurso restablecido a 'Disponible'.");
        } else {
            ConsoleUtils.printError("Error al procesar la devolución del préstamo.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewActiveLoans() {
        ConsoleUtils.printSection("PRÉSTAMOS ACTIVOS DE ESTUDIANTES");
        List<StudentLoan> sLoans = resourceController.getActiveStudentLoans();
        List<String> sHeaders = List.of("ID Préstamo", "Cód. Recurso", "Nombre del Recurso", "Estudiante", "Fecha Límite", "Estado");
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

        ConsoleUtils.printSection("PRÉSTAMOS ACTIVOS DE PROFESORES");
        List<TeacherLoan> tLoans = resourceController.getActiveTeacherLoans();
        List<String> tHeaders = List.of("ID Préstamo", "Cód. Recurso", "Nombre del Recurso", "Profesor", "Fecha Límite", "Estado");
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
        ConsoleUtils.printSection("REPORTE DE PRÉSTAMOS VENCIDOS (AUDITORÍA)");
        List<StudentLoan> sOverdue = resourceController.getOverdueStudentLoans();
        List<TeacherLoan> tOverdue = resourceController.getOverdueTeacherLoans();

        if (sOverdue.isEmpty() && tOverdue.isEmpty()) {
            ConsoleUtils.printSuccess("¡Excelente! No hay préstamos vencidos actualmente.");
        } else {
            if (!sOverdue.isEmpty()) {
                System.out.println("\nPréstamos Vencidos de Estudiantes:");
                List<String> sHeaders = List.of("ID Préstamo", "Recurso", "Nombre del Estudiante", "Fecha Límite Esperada");
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
                System.out.println("\nPréstamos Vencidos de Profesores:");
                List<String> tHeaders = List.of("ID Préstamo", "Recurso", "Nombre del Profesor", "Fecha Límite Esperada");
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
