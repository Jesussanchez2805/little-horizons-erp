package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.ResourceDAO;
import com.littlehorizons.erp.dao.ResourceDAOImpl;
import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.ResourceItem;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.util.AppLogger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller managing school resources, equipment inventory, student/teacher loans,
 * return tracking, and overdue resource identification.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ResourceController {

    private final ResourceDAO resourceDAO;

    public ResourceController() {
        this(new ResourceDAOImpl());
    }

    public ResourceController(ResourceDAO resourceDAO) {
        this.resourceDAO = resourceDAO;
    }

    // --- Inventory ---

    public List<ResourceCategory> getAllCategories() {
        return resourceDAO.findAllCategories();
    }

    public Optional<ResourceItem> createResource(ResourceItem resource) {
        return Optional.ofNullable(resourceDAO.createResource(resource));
    }

    public Optional<ResourceItem> getResourceById(int id) {
        return resourceDAO.findResourceById(id);
    }

    public Optional<ResourceItem> getResourceByCode(String code) {
        return resourceDAO.findResourceByCode(code);
    }

    public List<ResourceItem> getAllResources() {
        return resourceDAO.findAllResources();
    }

    public List<ResourceItem> getAvailableResources() {
        return resourceDAO.findAllResources().stream()
                .filter(ResourceItem::isAvailable)
                .collect(Collectors.toList());
    }

    public List<ResourceItem> getResourcesByCategory(int categoryId) {
        return resourceDAO.findResourcesByCategory(categoryId);
    }

    // --- Loans to Students ---

    /**
     * Issues a resource loan to a student, validating resource availability.
     */
    public Optional<StudentLoan> loanToStudent(int resourceId, int studentId, LocalDate returnDate, String obs) {
        Optional<ResourceItem> resOpt = resourceDAO.findResourceById(resourceId);
        if (resOpt.isEmpty()) {
            AppLogger.warning("Loan failed: Resource ID " + resourceId + " does not exist.");
            return Optional.empty();
        }

        ResourceItem res = resOpt.get();
        if (!res.isAvailable()) {
            AppLogger.warning("Loan failed: Resource '" + res.getName() + "' is not available (Current status: " + res.getStatus() + ")");
            return Optional.empty();
        }

        StudentLoan loan = new StudentLoan();
        loan.setResourceId(resourceId);
        loan.setStudentId(studentId);
        loan.setLoanDate(LocalDate.now());
        loan.setExpectedReturnDate(returnDate);
        loan.setStatus("Activo");
        loan.setObservations(obs);

        return Optional.ofNullable(resourceDAO.createStudentLoan(loan));
    }

    public boolean returnStudentLoan(int loanId, String observations) {
        return resourceDAO.returnStudentLoan(loanId, LocalDate.now(), observations);
    }

    public List<StudentLoan> getActiveStudentLoans() {
        return resourceDAO.findActiveStudentLoans();
    }

    public List<StudentLoan> getOverdueStudentLoans() {
        return resourceDAO.findOverdueStudentLoans();
    }

    // --- Loans to Teachers ---

    /**
     * Issues a resource loan to a teacher, validating resource availability.
     */
    public Optional<TeacherLoan> loanToTeacher(int resourceId, int teacherId, LocalDate returnDate, String obs) {
        Optional<ResourceItem> resOpt = resourceDAO.findResourceById(resourceId);
        if (resOpt.isEmpty()) {
            AppLogger.warning("Loan failed: Resource ID " + resourceId + " does not exist.");
            return Optional.empty();
        }

        ResourceItem res = resOpt.get();
        if (!res.isAvailable()) {
            AppLogger.warning("Loan failed: Resource '" + res.getName() + "' is not available.");
            return Optional.empty();
        }

        TeacherLoan loan = new TeacherLoan();
        loan.setResourceId(resourceId);
        loan.setTeacherId(teacherId);
        loan.setLoanDate(LocalDate.now());
        loan.setExpectedReturnDate(returnDate);
        loan.setStatus("Activo");
        loan.setObservations(obs);

        return Optional.ofNullable(resourceDAO.createTeacherLoan(loan));
    }

    public boolean returnTeacherLoan(int loanId, String observations) {
        return resourceDAO.returnTeacherLoan(loanId, LocalDate.now(), observations);
    }

    public List<TeacherLoan> getActiveTeacherLoans() {
        return resourceDAO.findActiveTeacherLoans();
    }

    public List<TeacherLoan> getOverdueTeacherLoans() {
        return resourceDAO.findOverdueTeacherLoans();
    }
}
