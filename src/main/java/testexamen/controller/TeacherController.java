/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testexamen.controller;

/**
 *
 * @author camper
 */

import testexamen.dao.TeacherDAO;
import testexamen.dao.TeacherDAOImpl;
import testexamen.model.Teacher;
import com.littlehorizons.erp.model.TeacherAssignment;
import com.littlehorizons.erp.util.AppLogger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller managing faculty records and course teaching assignments.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class TeacherController {

    private final TeacherDAO teacherDAO;

    public TeacherController() {
        this(new TeacherDAOImpl());
    }

    public TeacherController(TeacherDAO teacherDAO) {
        this.teacherDAO = teacherDAO;
    }

    public Optional<Teacher> registerTeacher(Teacher teacher) {
        if (teacher == null || teacher.getDocumentNumber() == null || teacher.getEmail() == null) {
            AppLogger.warning("Teacher registration validation failed.");
            return Optional.empty();
        }

        Optional<Teacher> existingDoc = teacherDAO.findByDocument(teacher.getDocumentNumber());
        if (existingDoc.isPresent()) {
            AppLogger.warning("Teacher with document " + teacher.getDocumentNumber() + " already exists.");
            return Optional.empty();
        }

        Optional<Teacher> existingEmail = teacherDAO.findByEmail(teacher.getEmail());
        if (existingEmail.isPresent()) {
            AppLogger.warning("Teacher with email " + teacher.getEmail() + " already exists.");
            return Optional.empty();
        }

        return Optional.ofNullable(teacherDAO.create(teacher));
    }

    public Optional<Teacher> getTeacherById(int id) {
        return teacherDAO.findById(id);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDAO.findAll();
    }

    public List<Teacher> getActiveTeachers() {
        return teacherDAO.findAll().stream()
                .filter(t -> "Activo".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean updateTeacher(Teacher teacher) {
        return teacherDAO.update(teacher);
    }

    public boolean deleteTeacher(int id) {
        return teacherDAO.delete(id);
    }

    public Optional<TeacherAssignment> assignCourse(int teacherId, int courseId, int year, String section) {
        TeacherAssignment ta = new TeacherAssignment();
        ta.setTeacherId(teacherId);
        ta.setCourseId(courseId);
        ta.setAcademicYear(year);
        ta.setSection(section);

        return Optional.ofNullable(teacherDAO.assignCourse(ta));
    }

    public List<TeacherAssignment> getTeacherAssignments(int teacherId, int year) {
        return teacherDAO.findAssignmentsByTeacher(teacherId, year);
    }

    public List<TeacherAssignment> getCourseAssignments(int courseId, int year) {
        return teacherDAO.findAssignmentsByCourse(courseId, year);
    }
}
