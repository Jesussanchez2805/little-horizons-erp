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
            ConsoleUtils.printHeader("MÓDULO 2: GESTIÓN ACADÉMICA Y CALIFICACIONES", "Little Horizons Educational ERP");
            System.out.println(" 1. Ver Catálogo de Grados y Cursos");
            System.out.println(" 2. Matricular Estudiante en Grado Académico");
            System.out.println(" 3. Inscribir Estudiante en Curso o Asignatura");
            System.out.println(" 4. Registrar Calificación de Estudiante (Nota 0.0 - 5.0)");
            System.out.println(" 5. Consultar Historial Académico y Promedio Acumulado (GPA)");
            System.out.println(" 6. Calcular Promedio General del Curso");
            System.out.println(" 7. Registrar Asistencia Diaria (Presente, Ausente, Tardanza, Excusa)");
            System.out.println(" 8. Ver Reporte de Asistencia y Ausentismo del Estudiante");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 8);

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
        ConsoleUtils.printSection("CATÁLOGO DE CURSOS ACADÉMICOS");
        List<Course> courses = academicController.getAllCourses();

        List<String> headers = List.of("ID", "Código", "Nombre del Curso", "Grado Académico", "Horas/Sem", "Activo");
        List<List<String>> rows = new ArrayList<>();
        for (Course c : courses) {
            rows.add(List.of(
                    String.valueOf(c.getId()),
                    c.getCode(),
                    c.getName(),
                    c.getGradeName() != null ? c.getGradeName() : String.valueOf(c.getGradeId()),
                    String.valueOf(c.getWeeklyHours()),
                    c.isActive() ? "SÍ" : "NO"
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void enrollStudentInGrade() {
        ConsoleUtils.printSection("MATRICULAR ESTUDIANTE EN GRADO ACADÉMICO");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);

        // Display grades
        List<GradeLevel> grades = academicController.getAllGradeLevels();
        System.out.println("\nGrados Académicos Disponibles:");
        for (GradeLevel gl : grades) {
            System.out.printf("  [%2d] %s (Nivel %d)%n", gl.getId(), gl.getName(), gl.getNumericLevel());
        }

        int gradeId = InputValidator.readInt("Seleccione ID del Grado Académico", 1, 12);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String obs = InputValidator.readOptionalString("Observaciones de Matrícula", "Matrícula regular");

        Optional<Enrollment> result = academicController.enrollStudent(studentId, gradeId, year, obs);
        if (result.isPresent()) {
            ConsoleUtils.printSuccess("Estudiante matriculado exitosamente en el grado con ID de matrícula: " + result.get().getId());
        } else {
            ConsoleUtils.printError("No se pudo matricular al estudiante. Ya podría estar matriculado para este año académico.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerStudentInCourse() {
        ConsoleUtils.printSection("INSCRIBIR ESTUDIANTE EN CURSO O ASIGNATURA");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        int courseId = InputValidator.readInt("Ingrese ID del Curso", 1, 999999);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Periodo Académico (I, II, III, IV)");
        String section = InputValidator.readOptionalString("Sección / Grupo", "A");

        Optional<CourseRegistration> reg = academicController.registerStudentInCourse(studentId, courseId, year, period, section);
        if (reg.isPresent()) {
            ConsoleUtils.printSuccess("Inscripción en el curso confirmada con ID: " + reg.get().getId());
        } else {
            ConsoleUtils.printError("Error al inscribir estudiante en el curso.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordGrade() {
        ConsoleUtils.printSection("REGISTRAR CALIFICACIÓN (NOTA 0.0 - 5.0)");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        int courseId = InputValidator.readInt("Ingrese ID del Curso", 1, 999999);
        int teacherId = InputValidator.readInt("Ingrese ID del Docente", 1, 999999);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Periodo Académico (I, II, III, IV, Final)");
        double grade = InputValidator.readDouble("Valor de la nota (0.0 a 5.0)", 0.0, 5.0);
        String obs = InputValidator.readOptionalString("Observaciones de la Evaluación", "Desempeño satisfactorio");

        Optional<AcademicRecord> rec = academicController.recordGrade(studentId, courseId, teacherId, year, period, grade, obs);
        if (rec.isPresent()) {
            ConsoleUtils.printSuccess(String.format("Calificación registrada: %.1f para el Estudiante %d en el Curso %d", grade, studentId, courseId));
        } else {
            ConsoleUtils.printError("Error al registrar la calificación.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void consultStudentHistoryAndGpa() {
        ConsoleUtils.printSection("HISTORIAL ACADÉMICO Y PROMEDIO (GPA)");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);

        List<AcademicRecord> grades = academicController.getStudentGrades(studentId);
        if (grades.isEmpty()) {
            ConsoleUtils.printWarning("No se encontraron registros de calificaciones para el Estudiante ID " + studentId);
        } else {
            List<String> headers = List.of("Año", "Periodo", "Cód. Curso", "Nombre del Curso", "Docente", "Nota", "Observaciones");
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
            ConsoleUtils.printSuccess(String.format(">> Promedio Ponderado Acumulado (GPA): %.2f / 5.0 <<", cumulativeGpa));

            System.out.println("\nPromedio por Asignatura:");
            Map<String, Double> bySubject = academicController.calculateAveragePerSubjectForStudent(studentId);
            bySubject.forEach((course, avg) -> System.out.printf("  • %-35s : %.2f / 5.0%n", course, avg));
        }
        InputValidator.pressEnterToContinue();
    }

    private void calculateCourseAverage() {
        ConsoleUtils.printSection("CALCULAR PROMEDIO GENERAL DEL CURSO");
        int courseId = InputValidator.readInt("Ingrese ID del Curso", 1, 999999);
        int year = InputValidator.readInt("Año Académico", 2020, 2035);
        String period = InputValidator.readNonEmptyString("Periodo Académico (I, II, III, IV, Final)");

        List<AcademicRecord> records = academicController.getCourseGrades(courseId, year, period);
        if (records.isEmpty()) {
            ConsoleUtils.printWarning("No hay registros de calificaciones para el Curso " + courseId + " en " + year + " " + period);
        } else {
            List<String> headers = List.of("Nombre del Estudiante", "Documento", "Nota", "Observaciones");
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
            ConsoleUtils.printSuccess(String.format("Promedio general del grupo para el Curso %d: %.2f / 5.0 (Total: %d estudiantes)",
                    courseId, courseAvg, records.size()));
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordAttendance() {
        ConsoleUtils.printSection("REGISTRAR ASISTENCIA DIARIA");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        int courseId = InputValidator.readInt("Ingrese ID del Curso", 1, 999999);
        LocalDate date = InputValidator.readDate("Fecha de Asistencia");

        System.out.println("Seleccione Estado:");
        System.out.println("1. Presente");
        System.out.println("2. Ausente");
        System.out.println("3. Tardanza");
        System.out.println("4. Excusa Médica");
        int st = InputValidator.readInt("Opción", 1, 4);
        String[] statuses = {"Presente", "Ausente", "Tardanza", "Excusa Médica"};
        String status = statuses[st - 1];

        String obs = InputValidator.readOptionalString("Observaciones", "");
        int teacherId = InputValidator.readInt("Registrado por Docente ID", 1, 999999);

        Optional<Attendance> att = academicController.markAttendance(studentId, courseId, date, status, obs, teacherId);
        if (att.isPresent()) {
            ConsoleUtils.printSuccess("Asistencia registrada: " + status + " el " + date);
        } else {
            ConsoleUtils.printError("Error al registrar asistencia.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewStudentAbsences() {
        ConsoleUtils.printSection("REPORTE DE ASISTENCIA Y AUSENCIAS DEL ESTUDIANTE");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);

        List<Attendance> absences = academicController.getStudentAbsences(studentId);
        if (absences.isEmpty()) {
            ConsoleUtils.printSuccess("El estudiante tiene excelente asistencia. No registra ausencias.");
        } else {
            List<String> headers = List.of("Fecha", "Curso", "Estado", "Observaciones");
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
                ConsoleUtils.printWarning(String.format("ALERTA: El estudiante ha acumulado %d ausencias/tardanzas. Se sugiere seguimiento con acudiente.", absences.size()));
            }
        }
        InputValidator.pressEnterToContinue();
    }
}
