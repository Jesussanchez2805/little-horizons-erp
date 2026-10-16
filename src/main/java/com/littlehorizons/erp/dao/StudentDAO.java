package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Student operations.
 * Follows the DAO pattern and Dependency Inversion Principle (DIP).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface StudentDAO {
    Student create(Student student);
    Optional<Student> findById(int id);
    Optional<Student> findByDocument(String docType, String docNumber);
    List<Student> findAll();
    List<Student> findByStatus(String status);
    boolean update(Student student);
    boolean delete(int id);
    boolean assignGuardian(int studentId, int guardianId, boolean isPrimary, boolean isEmergencyContact);
    List<Guardian> getGuardiansForStudent(int studentId);
}
