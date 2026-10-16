package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Guardian;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of StudentDAO using PreparedStatements and try-with-resources.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class StudentDAOImpl implements StudentDAO {

    @Override
    public Student create(Student student) {
        String sql = "INSERT INTO estudiantes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                     "primer_apellido, segundo_apellido, fecha_nacimiento, genero, direccion, telefono, " +
                     "correo_electronico, tipo_sangre, alergias, condiciones_medicas, fecha_ingreso, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getDocumentType());
            stmt.setString(2, student.getDocumentNumber());
            stmt.setString(3, student.getFirstName());
            stmt.setString(4, student.getSecondName());
            stmt.setString(5, student.getFirstLastName());
            stmt.setString(6, student.getSecondLastName());
            stmt.setDate(7, Date.valueOf(student.getBirthDate()));
            stmt.setString(8, student.getGender());
            stmt.setString(9, student.getAddress());
            stmt.setString(10, student.getPhone());
            stmt.setString(11, student.getEmail());
            stmt.setString(12, student.getBloodType());
            stmt.setString(13, student.getAllergies());
            stmt.setString(14, student.getMedicalConditions());
            stmt.setDate(15, Date.valueOf(student.getEntryDate()));
            stmt.setString(16, student.getStatus() != null ? student.getStatus() : "Activo");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Student created with ID " + student.getId() + ": " + student.getFullName());
                return student;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error inserting student: " + student.getFullName(), e);
        }
        return null;
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = "SELECT * FROM estudiantes WHERE id_estudiante = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding student by ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByDocument(String docType, String docNumber) {
        String sql = "SELECT * FROM estudiantes WHERE tipo_documento = ? AND numero_documento = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, docType);
            stmt.setString(2, docNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding student by doc: " + docType + " " + docNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes ORDER BY primer_apellido, segundo_apellido, primer_nombre";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all students", e);
        }
        return list;
    }

    @Override
    public List<Student> findByStatus(String status) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM estudiantes WHERE estado = ? ORDER BY primer_apellido, primer_nombre";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching students by status: " + status, e);
        }
        return list;
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE estudiantes SET tipo_documento = ?, numero_documento = ?, primer_nombre = ?, " +
                     "segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, fecha_nacimiento = ?, " +
                     "genero = ?, direccion = ?, telefono = ?, correo_electronico = ?, tipo_sangre = ?, " +
                     "alergias = ?, condiciones_medicas = ?, estado = ? WHERE id_estudiante = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getDocumentType());
            stmt.setString(2, student.getDocumentNumber());
            stmt.setString(3, student.getFirstName());
            stmt.setString(4, student.getSecondName());
            stmt.setString(5, student.getFirstLastName());
            stmt.setString(6, student.getSecondLastName());
            stmt.setDate(7, Date.valueOf(student.getBirthDate()));
            stmt.setString(8, student.getGender());
            stmt.setString(9, student.getAddress());
            stmt.setString(10, student.getPhone());
            stmt.setString(11, student.getEmail());
            stmt.setString(12, student.getBloodType());
            stmt.setString(13, student.getAllergies());
            stmt.setString(14, student.getMedicalConditions());
            stmt.setString(15, student.getStatus());
            stmt.setInt(16, student.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error updating student ID: " + student.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM estudiantes WHERE id_estudiante = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error deleting student ID: " + id, e);
            return false;
        }
    }

    @Override
    public boolean assignGuardian(int studentId, int guardianId, boolean isPrimary, boolean isEmergencyContact) {
        String sql = "INSERT INTO estudiante_acudiente (id_estudiante, id_acudiente, es_principal, es_contacto_emergencia) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE es_principal = VALUES(es_principal), es_contacto_emergencia = VALUES(es_contacto_emergencia)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, guardianId);
            stmt.setBoolean(3, isPrimary);
            stmt.setBoolean(4, isEmergencyContact);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error assigning guardian " + guardianId + " to student " + studentId, e);
            return false;
        }
    }

    @Override
    public List<Guardian> getGuardiansForStudent(int studentId) {
        List<Guardian> guardians = new ArrayList<>();
        String sql = "SELECT a.* FROM acudientes a " +
                     "INNER JOIN estudiante_acudiente ea ON a.id_acudiente = ea.id_acudiente " +
                     "WHERE ea.id_estudiante = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Guardian g = new Guardian();
                    g.setId(rs.getInt("id_acudiente"));
                    g.setDocumentType(rs.getString("tipo_documento"));
                    g.setDocumentNumber(rs.getString("numero_documento"));
                    g.setFirstName(rs.getString("primer_nombre"));
                    g.setSecondName(rs.getString("segundo_nombre"));
                    g.setFirstLastName(rs.getString("primer_apellido"));
                    g.setSecondLastName(rs.getString("segundo_apellido"));
                    g.setRelationship(rs.getString("parentesco"));
                    g.setPrimaryPhone(rs.getString("telefono_principal"));
                    g.setSecondaryPhone(rs.getString("telefono_secundario"));
                    g.setEmail(rs.getString("correo_electronico"));
                    g.setAddress(rs.getString("direccion"));
                    g.setOccupation(rs.getString("ocupacion"));
                    g.setFinancialResponsible(rs.getBoolean("es_responsable_financiero"));
                    guardians.add(g);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching guardians for student: " + studentId, e);
        }
        return guardians;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id_estudiante"));
        s.setDocumentType(rs.getString("tipo_documento"));
        s.setDocumentNumber(rs.getString("numero_documento"));
        s.setFirstName(rs.getString("primer_nombre"));
        s.setSecondName(rs.getString("segundo_nombre"));
        s.setFirstLastName(rs.getString("primer_apellido"));
        s.setSecondLastName(rs.getString("segundo_apellido"));
        Date birth = rs.getDate("fecha_nacimiento");
        if (birth != null) s.setBirthDate(birth.toLocalDate());
        s.setGender(rs.getString("genero"));
        s.setAddress(rs.getString("direccion"));
        s.setPhone(rs.getString("telefono"));
        s.setEmail(rs.getString("correo_electronico"));
        s.setBloodType(rs.getString("tipo_sangre"));
        s.setAllergies(rs.getString("alergias"));
        s.setMedicalConditions(rs.getString("condiciones_medicas"));
        Date entry = rs.getDate("fecha_ingreso");
        if (entry != null) s.setEntryDate(entry.toLocalDate());
        s.setStatus(rs.getString("estado"));
        return s;
    }
}
