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
            ConsoleUtils.printHeader("MÓDULO 1: GESTIÓN DE ESTUDIANTES Y ACUDIENTES", "Little Horizons Educational ERP");
            System.out.println(" 1. Registrar Nuevo Estudiante");
            System.out.println(" 2. Listar Directorio de Estudiantes");
            System.out.println(" 3. Buscar Estudiante por ID / Documento");
            System.out.println(" 4. Registrar Acudiente (Tutor Legal)");
            System.out.println(" 5. Asociar Acudiente a Estudiante");
            System.out.println(" 6. Ver Acudientes y Contactos de Emergencia");
            System.out.println(" 7. Actualizar Estado del Estudiante");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 7);

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
        ConsoleUtils.printSection("REGISTRAR NUEVO ESTUDIANTE");

        String docType = InputValidator.readNonEmptyString("Tipo de Documento (TI, CC, CE, Pasaporte)");
        String docNumber = InputValidator.readNonEmptyString("Número de Documento");
        String firstName = InputValidator.readNonEmptyString("Primer Nombre");
        String secondName = InputValidator.readOptionalString("Segundo Nombre", "");
        String firstLastName = InputValidator.readNonEmptyString("Primer Apellido");
        String secondLastName = InputValidator.readOptionalString("Segundo Apellido", "");
        LocalDate birthDate = InputValidator.readDate("Fecha de Nacimiento");
        String gender = InputValidator.readNonEmptyString("Género (M, F, Otro)");
        String address = InputValidator.readNonEmptyString("Dirección de Residencia");
        String phone = InputValidator.readNonEmptyString("Teléfono de Contacto");
        String email = InputValidator.readEmail("Correo Electrónico (Institucional / Personal)");
        String bloodType = InputValidator.readNonEmptyString("Tipo de Sangre (ej. O+, A+, B-)");
        String allergies = InputValidator.readOptionalString("Alergias", "Ninguna");
        String medicalConditions = InputValidator.readOptionalString("Condiciones Médicas", "Ninguna");

        Student student = new Student(0, docType, docNumber, firstName, secondName, firstLastName,
                secondLastName, birthDate, gender, address, phone, email, bloodType, allergies,
                medicalConditions, LocalDate.now(), "Activo");

        Optional<Student> result = studentController.registerStudent(student);
        if (result.isPresent()) {
            ConsoleUtils.printSuccess("Estudiante registrado exitosamente con ID: " + result.get().getId());
        } else {
            ConsoleUtils.printError("Error al registrar estudiante. El número de documento ya podría existir.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void listAllStudents() {
        ConsoleUtils.printSection("DIRECTORIO DE ESTUDIANTES");
        List<Student> students = studentController.getAllStudents();

        List<String> headers = List.of("ID", "Tipo Doc.", "N° Documento", "Nombre Completo", "Género", "Teléfono", "Estado");
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
        ConsoleUtils.printInfo("Total de estudiantes registrados: " + students.size());
        InputValidator.pressEnterToContinue();
    }

    private void searchStudent() {
        ConsoleUtils.printSection("BUSCAR ESTUDIANTE");
        System.out.println("1. Buscar por ID de Base de Datos");
        System.out.println("2. Buscar por Documento de Identidad");
        int mode = InputValidator.readInt("Seleccione tipo de búsqueda", 1, 2);

        Optional<Student> studentOpt = Optional.empty();
        if (mode == 1) {
            int id = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
            studentOpt = studentController.getStudentById(id);
        } else {
            String docType = InputValidator.readNonEmptyString("Tipo de Documento (TI, CC, CE, Pasaporte)");
            String docNumber = InputValidator.readNonEmptyString("Número de Documento");
            studentOpt = studentController.getStudentByDocument(docType, docNumber);
        }

        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            ConsoleUtils.printSuccess("Estudiante encontrado:");
            System.out.println("  • ID:                  " + s.getId());
            System.out.println("  • Nombre Completo:     " + s.getFullName());
            System.out.println("  • Documento:           " + s.getDocumentType() + " " + s.getDocumentNumber());
            System.out.println("  • Fecha Nacimiento:    " + s.getBirthDate());
            System.out.println("  • Género:              " + s.getGender());
            System.out.println("  • Correo Electrónico:  " + s.getEmail());
            System.out.println("  • Teléfono:            " + s.getPhone());
            System.out.println("  • Dirección:           " + s.getAddress());
            System.out.println("  • Tipo de Sangre:      " + s.getBloodType());
            System.out.println("  • Alergias:            " + s.getAllergies());
            System.out.println("  • Condiciones Médicas: " + s.getMedicalConditions());
            System.out.println("  • Estado:              " + s.getStatus());
        } else {
            ConsoleUtils.printError("Estudiante no encontrado.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerGuardian() {
        ConsoleUtils.printSection("REGISTRAR ACUDIENTE O TUTOR LEGAL");

        String docType = InputValidator.readNonEmptyString("Tipo de Documento (CC, CE, Pasaporte)");
        String docNumber = InputValidator.readNonEmptyString("Número de Documento");
        String firstName = InputValidator.readNonEmptyString("Primer Nombre");
        String secondName = InputValidator.readOptionalString("Segundo Nombre", "");
        String firstLastName = InputValidator.readNonEmptyString("Primer Apellido");
        String secondLastName = InputValidator.readOptionalString("Segundo Apellido", "");
        String relationship = InputValidator.readNonEmptyString("Parentesco (Padre, Madre, Abuelo, Tutor Legal)");
        String primaryPhone = InputValidator.readNonEmptyString("Teléfono Principal");
        String secondaryPhone = InputValidator.readOptionalString("Teléfono Secundario", "");
        String email = InputValidator.readEmail("Correo Electrónico");
        String address = InputValidator.readNonEmptyString("Dirección");
        String occupation = InputValidator.readOptionalString("Ocupación", "Independiente");
        boolean financial = InputValidator.readBoolean("¿Es responsable financiero de pagos?");

        Guardian g = new Guardian(0, docType, docNumber, firstName, secondName, firstLastName,
                secondLastName, relationship, primaryPhone, secondaryPhone, email, address, occupation, financial);

        Optional<Guardian> res = studentController.registerGuardian(g);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Acudiente registrado con ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Error al registrar acudiente.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void linkGuardianToStudent() {
        ConsoleUtils.printSection("ASOCIAR ACUDIENTE A ESTUDIANTE");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        int guardianId = InputValidator.readInt("Ingrese ID del Acudiente", 1, 999999);
        boolean isPrimary = InputValidator.readBoolean("¿Es contacto principal de comunicación?");
        boolean isEmergency = InputValidator.readBoolean("¿Es contacto de emergencia?");

        boolean ok = studentController.linkStudentToGuardian(studentId, guardianId, isPrimary, isEmergency);
        if (ok) {
            ConsoleUtils.printSuccess("Acudiente asociado exitosamente al estudiante.");
        } else {
            ConsoleUtils.printError("No se pudo asociar el acudiente. Verifique que ambos IDs existan en la base de datos.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentGuardians() {
        ConsoleUtils.printSection("ACUDIENTES Y CONTACTOS DE EMERGENCIA");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);

        List<Guardian> guardians = studentController.getGuardiansForStudent(studentId);
        if (guardians.isEmpty()) {
            ConsoleUtils.printWarning("No hay acudientes asociados al Estudiante ID " + studentId);
        } else {
            List<String> headers = List.of("ID", "Nombre Completo", "Parentesco", "Teléfono Principal", "Correo Electrónico", "Resp. Financiero");
            List<List<String>> rows = new ArrayList<>();
            for (Guardian g : guardians) {
                rows.add(List.of(
                        String.valueOf(g.getId()),
                        g.getFullName(),
                        g.getRelationship(),
                        g.getPrimaryPhone(),
                        g.getEmail(),
                        g.isFinancialResponsible() ? "SÍ" : "NO"
                ));
            }
            ConsoleUtils.printTable(headers, rows);
        }
        InputValidator.pressEnterToContinue();
    }

    private void updateStudentStatus() {
        ConsoleUtils.printSection("ACTUALIZAR ESTADO DEL ESTUDIANTE");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);

        Optional<Student> opt = studentController.getStudentById(studentId);
        if (opt.isEmpty()) {
            ConsoleUtils.printError("Estudiante no encontrado.");
            InputValidator.pressEnterToContinue();
            return;
        }

        Student s = opt.get();
        System.out.println("Estado actual: " + s.getStatus());
        System.out.println("1. Activo");
        System.out.println("2. Graduado");
        System.out.println("3. Retirado");
        System.out.println("4. Suspendido");
        int ch = InputValidator.readInt("Seleccione nuevo estado", 1, 4);
        String[] statuses = {"Activo", "Graduado", "Retirado", "Suspendido"};
        s.setStatus(statuses[ch - 1]);

        boolean updated = studentController.updateStudent(s);
        if (updated) {
            ConsoleUtils.printSuccess("Estado del estudiante actualizado a: " + s.getStatus());
        } else {
            ConsoleUtils.printError("Error al actualizar el estado.");
        }
        InputValidator.pressEnterToContinue();
    }
}
