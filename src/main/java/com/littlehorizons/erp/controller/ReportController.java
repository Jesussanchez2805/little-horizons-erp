package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.*;
import com.littlehorizons.erp.model.AcademicRecord;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.ResourceItem;
import com.littlehorizons.erp.model.Student;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller consolidating executive reports and business intelligence statistics
 * using Java Stream API and Functional Interfaces.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ReportController {

    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;
    private final AcademicDAO academicDAO;
    private final FinancialDAO financialDAO;
    private final ResourceDAO resourceDAO;
    private final ExtracurricularDAO extracurricularDAO;

    public ReportController() {
        this.studentDAO = new StudentDAOImpl();
        this.teacherDAO = new TeacherDAOImpl();
        this.academicDAO = new AcademicDAOImpl();
        this.financialDAO = new FinancialDAOImpl();
        this.resourceDAO = new ResourceDAOImpl();
        this.extracurricularDAO = new ExtracurricularDAOImpl();
    }

    /**
     * Aggregates executive dashboard metrics.
     */
    public Map<String, Object> getExecutiveDashboardMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();

        List<Student> students = studentDAO.findAll();
        long activeStudents = students.stream().filter(s -> "Activo".equalsIgnoreCase(s.getStatus())).count();

        int facultyCount = teacherDAO.findAll().size();
        int coursesCount = academicDAO.findAllCourses().size();
        int clubsCount = extracurricularDAO.findAllClubs().size();
        int resourcesCount = resourceDAO.findAllResources().size();

        List<Payment> payments = financialDAO.findAllPayments();
        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("Total de Estudiantes Registrados", students.size());
        metrics.put("Estudiantes Activos", activeStudents);
        metrics.put("Docentes / Profesores", facultyCount);
        metrics.put("Cursos y Asignaturas", coursesCount);
        metrics.put("Clubes Extracurriculares", clubsCount);
        metrics.put("Recursos en Inventario", resourcesCount);
        metrics.put("Total de Ingresos Recaudados", totalRevenue);

        return metrics;
    }

    /**
     * Top students ranking by GPA using Stream API sorting and limiting.
     */
    public List<Map.Entry<String, Double>> getTopStudentsByGpa(int limit) {
        List<Student> students = studentDAO.findAll();
        Map<String, Double> studentGpas = new HashMap<>();

        for (Student s : students) {
            List<AcademicRecord> grades = academicDAO.findGradesByStudent(s.getId());
            if (!grades.isEmpty()) {
                double avg = grades.stream()
                        .filter(g -> g.getGradeValue() != null)
                        .mapToDouble(g -> g.getGradeValue().doubleValue())
                        .average()
                        .orElse(0.0);
                studentGpas.put(s.getFullName() + " (" + s.getDocumentNumber() + ")", avg);
            }
        }

        return studentGpas.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Summarizes financial revenues grouped by payment concept name.
     */
    public Map<String, BigDecimal> getRevenueByConcept() {
        List<Payment> payments = financialDAO.findAllPayments();
        return payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getConceptName() != null ? p.getConceptName() : "General",
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmountPaid, BigDecimal::add)
                ));
    }

    /**
     * Resource inventory breakdown by status.
     */
    public Map<String, Long> getResourceInventoryBreakdown() {
        List<ResourceItem> items = resourceDAO.findAllResources();
        return items.stream()
                .collect(Collectors.groupingBy(ResourceItem::getStatus, Collectors.counting()));
    }
}
