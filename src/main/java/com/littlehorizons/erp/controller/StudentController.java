package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.GuardianDAO;
import com.littlehorizons.erp.dao.GuardianDAOImpl;
import com.littlehorizons.erp.dao.StudentDAO;
import com.littlehorizons.erp.dao.StudentDAOImpl;
import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.AppLogger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller managing student admissions, profile updates, and guardian associations.
 * Applies the Single Responsibility Principle (SRP) and uses Stream API for filtering.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class StudentController {

    private final StudentDAO studentDAO;
    private final GuardianDAO guardianDAO;

    public StudentController() {
        this(new StudentDAOImpl(), new GuardianDAOImpl());
    }

    public StudentController(StudentDAO studentDAO, GuardianDAO guardianDAO) {
        this.studentDAO = studentDAO;
        this.guardianDAO = guardianDAO;
    }

    /**
     * Registers a new student, checking for duplicate identity documents.
     *
     * @param student Student entity
     * @return Registered student with generated ID, or empty Optional if validation fails
     */
    public Optional<Student> registerStudent(Student student) {
        if (student == null || student.getDocumentNumber() == null || student.getFirstName() == null) {
            AppLogger.warning("Validation failed: Student object or mandatory fields are null.");
            return Optional.empty();
        }

        // Check if student with document already exists
        Optional<Student> existing = studentDAO.findByDocument(student.getDocumentType(), student.getDocumentNumber());
        if (existing.isPresent()) {
            AppLogger.warning("Registration rejected: Document already registered (" + student.getDocumentNumber() + ")");
            return Optional.empty();
        }

        Student created = studentDAO.create(student);
        return Optional.ofNullable(created);
    }

    /**
     * Registers a new legal guardian.
     */
    public Optional<Guardian> registerGuardian(Guardian guardian) {
        if (guardian == null || guardian.getDocumentNumber() == null) {
            return Optional.empty();
        }

        Optional<Guardian> existing = guardianDAO.findByDocument(guardian.getDocumentNumber());
        if (existing.isPresent()) {
            return existing; // Return existing guardian
        }

        return Optional.ofNullable(guardianDAO.create(guardian));
    }

    /**
     * Associates a student with a legal guardian.
     */
    public boolean linkStudentToGuardian(int studentId, int guardianId, boolean isPrimary, boolean isEmergency) {
        return studentDAO.assignGuardian(studentId, guardianId, isPrimary, isEmergency);
    }

    public Optional<Student> getStudentById(int id) {
        return studentDAO.findById(id);
    }

    public Optional<Student> getStudentByDocument(String docType, String docNumber) {
        return studentDAO.findByDocument(docType, docNumber);
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * Filters active students using Java Stream API.
     */
    public List<Student> getActiveStudents() {
        return studentDAO.findAll().stream()
                .filter(s -> "Activo".equalsIgnoreCase(s.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Guardian> getGuardiansForStudent(int studentId) {
        return studentDAO.getGuardiansForStudent(studentId);
    }

    public List<Guardian> getAllGuardians() {
        return guardianDAO.findAll();
    }

    public boolean updateStudent(Student student) {
        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id) {
        return studentDAO.delete(id);
    }
}
