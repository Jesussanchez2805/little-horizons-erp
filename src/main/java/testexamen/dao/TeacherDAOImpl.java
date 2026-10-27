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
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of TeacherDAO.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class TeacherDAOImpl implements TeacherDAO {

    @Override
    public Teacher create(Teacher t) {
        String sql = "INSERT INTO profesores (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                     "primer_apellido, segundo_apellido, correo_electronico, telefono, especializacion, fecha_contratacion, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, t.getDocumentType());
            stmt.setString(2, t.getDocumentNumber());
            stmt.setString(3, t.getFirstName());
            stmt.setString(4, t.getSecondName());
            stmt.setString(5, t.getFirstLastName());
            stmt.setString(6, t.getSecondLastName());
            stmt.setString(7, t.getEmail());
            stmt.setString(8, t.getPhone());
            stmt.setString(9, t.getSpecialization());
            stmt.setDate(10, Date.valueOf(t.getHireDate()));
            stmt.setString(11, t.getStatus() != null ? t.getStatus() : "Activo");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        t.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Teacher created: " + t.getFullName());
                return t;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error inserting teacher: " + t.getFullName(), e);
        }
        return null;
    }

    @Override
    public Optional<Teacher> findById(int id) {
        String sql = "SELECT * FROM profesores WHERE id_profesor = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding teacher ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Teacher> findByDocument(String docNumber) {
        String sql = "SELECT * FROM profesores WHERE numero_documento = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, docNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding teacher doc: " + docNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Teacher> findByEmail(String email) {
        String sql = "SELECT * FROM profesores WHERE correo_electronico = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding teacher by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM profesores ORDER BY primer_apellido, primer_nombre";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTeacher(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding all teachers", e);
        }
        return list;
    }

    @Override
    public boolean update(Teacher t) {
        String sql = "UPDATE profesores SET tipo_documento = ?, numero_documento = ?, primer_nombre = ?, " +
                     "segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, correo_electronico = ?, " +
                     "telefono = ?, especializacion = ?, estado = ? WHERE id_profesor = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getDocumentType());
            stmt.setString(2, t.getDocumentNumber());
            stmt.setString(3, t.getFirstName());
            stmt.setString(4, t.getSecondName());
            stmt.setString(5, t.getFirstLastName());
            stmt.setString(6, t.getSecondLastName());
            stmt.setString(7, t.getEmail());
            stmt.setString(8, t.getPhone());
            stmt.setString(9, t.getSpecialization());
            stmt.setString(10, t.getStatus());
            stmt.setInt(11, t.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error updating teacher ID: " + t.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM profesores WHERE id_profesor = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error deleting teacher ID: " + id, e);
            return false;
        }
    }

    @Override
    public TeacherAssignment assignCourse(TeacherAssignment assignment) {
        String sql = "INSERT INTO asignaciones_profesor (id_profesor, id_materia, año_academico, seccion) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, assignment.getTeacherId());
            stmt.setInt(2, assignment.getCourseId());
            stmt.setInt(3, assignment.getAcademicYear());
            stmt.setString(4, assignment.getSection() != null ? assignment.getSection() : "A");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        assignment.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Assigned teacher " + assignment.getTeacherId() + " to course " + assignment.getCourseId());
                return assignment;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error creating teacher course assignment", e);
        }
        return null;
    }

    @Override
    public List<TeacherAssignment> findAssignmentsByTeacher(int teacherId, int academicYear) {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ap.*, m.nombre_materia, m.codigo_materia, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor " +
                     "FROM asignaciones_profesor ap " +
                     "INNER JOIN materias m ON ap.id_materia = m.id_materia " +
                     "INNER JOIN profesores p ON ap.id_profesor = p.id_profesor " +
                     "WHERE ap.id_profesor = ? AND ap.año_academico = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            stmt.setInt(2, academicYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TeacherAssignment ta = new TeacherAssignment();
                    ta.setId(rs.getInt("id_asignacion"));
                    ta.setTeacherId(rs.getInt("id_profesor"));
                    ta.setCourseId(rs.getInt("id_materia"));
                    ta.setAcademicYear(rs.getInt("año_academico"));
                    ta.setSection(rs.getString("seccion"));
                    ta.setCourseName(rs.getString("nombre_materia"));
                    ta.setCourseCode(rs.getString("codigo_materia"));
                    ta.setTeacherName(rs.getString("nom_profesor"));
                    list.add(ta);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching assignments for teacher: " + teacherId, e);
        }
        return list;
    }

    @Override
    public List<TeacherAssignment> findAssignmentsByCourse(int courseId, int academicYear) {
        List<TeacherAssignment> list = new ArrayList<>();
        String sql = "SELECT ap.*, m.nombre_materia, m.codigo_materia, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor " +
                     "FROM asignaciones_profesor ap " +
                     "INNER JOIN materias m ON ap.id_materia = m.id_materia " +
                     "INNER JOIN profesores p ON ap.id_profesor = p.id_profesor " +
                     "WHERE ap.id_materia = ? AND ap.año_academico = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, academicYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TeacherAssignment ta = new TeacherAssignment();
                    ta.setId(rs.getInt("id_asignacion"));
                    ta.setTeacherId(rs.getInt("id_profesor"));
                    ta.setCourseId(rs.getInt("id_materia"));
                    ta.setAcademicYear(rs.getInt("año_academico"));
                    ta.setSection(rs.getString("seccion"));
                    ta.setCourseName(rs.getString("nombre_materia"));
                    ta.setCourseCode(rs.getString("codigo_materia"));
                    ta.setTeacherName(rs.getString("nom_profesor"));
                    list.add(ta);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching assignments for course: " + courseId, e);
        }
        return list;
    }

    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id_profesor"));
        t.setDocumentType(rs.getString("tipo_documento"));
        t.setDocumentNumber(rs.getString("numero_documento"));
        t.setFirstName(rs.getString("primer_nombre"));
        t.setSecondName(rs.getString("segundo_nombre"));
        t.setFirstLastName(rs.getString("primer_apellido"));
        t.setSecondLastName(rs.getString("segundo_apellido"));
        t.setEmail(rs.getString("correo_electronico"));
        t.setPhone(rs.getString("telefono"));
        t.setSpecialization(rs.getString("especializacion"));
        Date hire = rs.getDate("fecha_contratacion");
        if (hire != null) t.setHireDate(hire.toLocalDate());
        t.setStatus(rs.getString("estado"));
        return t;
    }
}
