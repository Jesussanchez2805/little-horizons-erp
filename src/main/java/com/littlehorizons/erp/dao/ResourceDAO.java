package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.ResourceItem;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Resource Management operations:
 * Inventory, Categories, Student Loans, Teacher Loans, and Returns.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface ResourceDAO {
    // Categories
    List<ResourceCategory> findAllCategories();

    // Resources
    ResourceItem createResource(ResourceItem resource);
    Optional<ResourceItem> findResourceById(int id);
    Optional<ResourceItem> findResourceByCode(String code);
    List<ResourceItem> findAllResources();
    List<ResourceItem> findResourcesByCategory(int categoryId);
    boolean updateResourceStatus(int resourceId, String status);

    // Student Loans
    StudentLoan createStudentLoan(StudentLoan loan);
    Optional<StudentLoan> findStudentLoanById(int id);
    List<StudentLoan> findActiveStudentLoans();
    List<StudentLoan> findOverdueStudentLoans();
    boolean returnStudentLoan(int loanId, LocalDate returnDate, String observations);

    // Teacher Loans
    TeacherLoan createTeacherLoan(TeacherLoan loan);
    Optional<TeacherLoan> findTeacherLoanById(int id);
    List<TeacherLoan> findActiveTeacherLoans();
    List<TeacherLoan> findOverdueTeacherLoans();
    boolean returnTeacherLoan(int loanId, LocalDate returnDate, String observations);
}
