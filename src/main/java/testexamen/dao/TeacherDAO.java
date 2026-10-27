/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testexamen.dao;

/**
 *
 * @author camper
 */

import testexamen.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Teacher operations.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface TeacherDAO {
    Teacher create(Teacher teacher);
    Optional<Teacher> findById(int id);
    Optional<Teacher> findByDocument(String docNumber);
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findAll();
    boolean update(Teacher teacher);
    boolean delete(int id);

    // Course assignments
    TeacherAssignment assignCourse(TeacherAssignment assignment);
    List<TeacherAssignment> findAssignmentsByTeacher(int teacherId, int academicYear);
    List<TeacherAssignment> findAssignmentsByCourse(int courseId, int academicYear);
}
