package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Guardian operations.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface GuardianDAO {
    Guardian create(Guardian guardian);
    Optional<Guardian> findById(int id);
    Optional<Guardian> findByDocument(String docNumber);
    List<Guardian> findAll();
    boolean update(Guardian guardian);
    boolean delete(int id);
    List<Student> getStudentsForGuardian(int guardianId);
}
