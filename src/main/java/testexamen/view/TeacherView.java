/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testexamen.view;

/**
 *
 * @author camper
 */

import testexamen.controller.TeacherController;
import testexamen.model.Teacher;
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
            ConsoleUtils.printHeader("GESTIÓN DE PROFESORES Y CARGA ACADÉMICA", "Little Horizons Educational ERP");
            System.out.println(" 1. Ver Profesores Asignados a un Curso");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 6);

            switch (choice) {

                case 1 -> viewTeacherWorkload();
                case 0 -> back = true;
            }
        }
    }

    private void listFacultyDirectory() {
        ConsoleUtils.printSection("DIRECTORIO DE PROFESORES");
        List<Teacher> teachers = teacherController.getAllTeachers();

        List<String> headers = List.of("ID", "N° Documento", "Nombre Completo", "Especialidad", "Correo Electrónico", "Teléfono", "Estado");
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
        ConsoleUtils.printInfo("Total de profesores registrados: " + teachers.size());
        InputValidator.pressEnterToContinue();
    }

    private void registerTeacher() {
        ConsoleUtils.printSection("REGISTRAR NUEVO PROFESOR");

        String docType = InputValidator.readNonEmptyString("Tipo de Documento (CC, CE, Pasaporte)");
        String docNumber = InputValidator.readNonEmptyString("Número de Documento");
        String firstName = InputValidator.readNonEmptyString("Primer Nombre");
        String secondName = InputValidator.readOptionalString("Segundo Nombre", "");
        String firstLastName = InputValidator.readNonEmptyString("Primer Apellido");
        String secondLastName = InputValidator.readOptionalString("Segundo Apellido", "");
        String email = InputValidator.readEmail("Correo Electrónico");
        String phone = InputValidator.readNonEmptyString("Teléfono");
        String spec = InputValidator.readNonEmptyString("Especialidad / Departamento");
        LocalDate hire = InputValidator.readDate("Fecha de Contratación");

        Teacher t = new Teacher(0, docType, docNumber, firstName, secondName, firstLastName,
                secondLastName, email, phone, spec, hire, "Activo");

        Optional<Teacher> res = teacherController.registerTeacher(t);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Profesor registrado exitosamente con ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Error al registrar profesor. Verifique si el documento o correo ya existen.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void searchTeacher() {
        ConsoleUtils.printSection("BUSCAR PROFESOR");
        int id = InputValidator.readInt("Ingrese ID del Profesor", 1, 999999);

        Optional<Teacher> opt = teacherController.getTeacherById(id);
        if (opt.isPresent()) {
            Teacher t = opt.get();
            ConsoleUtils.printSuccess("Detalles del profesor:");
            System.out.println("  • ID:             " + t.getId());
            System.out.println("  • Nombre:         " + t.getFullName());
            System.out.println("  • Documento:      " + t.getDocumentType() + " " + t.getDocumentNumber());
            System.out.println("  • Especialidad:   " + t.getSpecialization());
            System.out.println("  • Correo:         " + t.getEmail());
            System.out.println("  • Teléfono:       " + t.getPhone());
            System.out.println("  • Fecha Contrato: " + t.getHireDate());
            System.out.println("  • Estado:         " + t.getStatus());
        } else {
            ConsoleUtils.printError("Profesor no encontrado.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void assignCourseToTeacher() {
        ConsoleUtils.printSection("ASIGNAR CURSO A PROFESOR (CARGA ACADÉMICA)");
        int teacherId = InputValidator.readInt("Ingrese ID del Profesor", 1, 999999);
        int courseId = InputValidator.readInt("Ingrese ID del Curso", 1, 999999);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String section = InputValidator.readOptionalString("Sección / Grupo", "A");

        Optional<TeacherAssignment> res = teacherController.assignCourse(teacherId, courseId, year, section);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Curso asignado exitosamente. ID de Asignación: " + res.get().getId());
        } else {
            ConsoleUtils.printError("No se pudo asignar el curso. Verifique si el profesor o el curso existen.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewTeacherWorkload() {
        ConsoleUtils.printSection("CARGA ACADÉMICA Y ASIGNACIONES DEL PROFESOR");
        int teacherId = InputValidator.readInt("Ingrese ID del Profesor", 1, 999999);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);

        List<TeacherAssignment> list = teacherController.getTeacherAssignments(teacherId, year);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No se encontraron asignaciones para el Profesor ID " + teacherId + " en el año " + year);
        } else {
            List<String> headers = List.of("ID Asignación", "Cód. Curso", "Nombre del Curso", "Sección", "Año");
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
        ConsoleUtils.printSection("PROFESORES ASIGNADOS AL CURSO");
        int courseId = InputValidator.readInt("Ingrese ID del Curso", 1, 999999);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);

        List<TeacherAssignment> list = teacherController.getCourseAssignments(courseId, year);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No se encontraron profesores asignados para el Curso ID " + courseId + " en el año " + year);
        } else {
            List<String> headers = List.of("ID Asignación", "Nombre del Profesor", "Sección", "Año");
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
