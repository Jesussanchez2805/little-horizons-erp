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
 * JDBC implementation of GuardianDAO.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class GuardianDAOImpl implements GuardianDAO {

    @Override
    public Guardian create(Guardian g) {
        String sql = "INSERT INTO acudientes (tipo_documento, numero_documento, primer_nombre, segundo_nombre, " +
                     "primer_apellido, segundo_apellido, parentesco, telefono_principal, telefono_secundario, " +
                     "correo_electronico, direccion, ocupacion, es_responsable_financiero) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, g.getDocumentType());
            stmt.setString(2, g.getDocumentNumber());
            stmt.setString(3, g.getFirstName());
            stmt.setString(4, g.getSecondName());
            stmt.setString(5, g.getFirstLastName());
            stmt.setString(6, g.getSecondLastName());
            stmt.setString(7, g.getRelationship());
            stmt.setString(8, g.getPrimaryPhone());
            stmt.setString(9, g.getSecondaryPhone());
            stmt.setString(10, g.getEmail());
            stmt.setString(11, g.getAddress());
            stmt.setString(12, g.getOccupation());
            stmt.setBoolean(13, g.isFinancialResponsible());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        g.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Guardian created ID " + g.getId() + ": " + g.getFullName());
                return g;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error inserting guardian: " + g.getFullName(), e);
        }
        return null;
    }

    @Override
    public Optional<Guardian> findById(int id) {
        String sql = "SELECT * FROM acudientes WHERE id_acudiente = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuardian(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding guardian ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Guardian> findByDocument(String docNumber) {
        String sql = "SELECT * FROM acudientes WHERE numero_documento = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, docNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGuardian(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding guardian doc: " + docNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Guardian> findAll() {
        List<Guardian> list = new ArrayList<>();
        String sql = "SELECT * FROM acudientes ORDER BY primer_apellido, primer_nombre";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToGuardian(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding all guardians", e);
        }
        return list;
    }

    @Override
    public boolean update(Guardian g) {
        String sql = "UPDATE acudientes SET tipo_documento = ?, numero_documento = ?, primer_nombre = ?, " +
                     "segundo_nombre = ?, primer_apellido = ?, segundo_apellido = ?, parentesco = ?, " +
                     "telefono_principal = ?, telefono_secundario = ?, correo_electronico = ?, direccion = ?, " +
                     "ocupacion = ?, es_responsable_financiero = ? WHERE id_acudiente = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, g.getDocumentType());
            stmt.setString(2, g.getDocumentNumber());
            stmt.setString(3, g.getFirstName());
            stmt.setString(4, g.getSecondName());
            stmt.setString(5, g.getFirstLastName());
            stmt.setString(6, g.getSecondLastName());
            stmt.setString(7, g.getRelationship());
            stmt.setString(8, g.getPrimaryPhone());
            stmt.setString(9, g.getSecondaryPhone());
            stmt.setString(10, g.getEmail());
            stmt.setString(11, g.getAddress());
            stmt.setString(12, g.getOccupation());
            stmt.setBoolean(13, g.isFinancialResponsible());
            stmt.setInt(14, g.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error updating guardian ID: " + g.getId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM acudientes WHERE id_acudiente = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error deleting guardian ID: " + id, e);
            return false;
        }
    }

    @Override
    public List<Student> getStudentsForGuardian(int guardianId) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT e.* FROM estudiantes e " +
                     "INNER JOIN estudiante_acudiente ea ON e.id_estudiante = ea.id_estudiante " +
                     "WHERE ea.id_acudiente = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guardianId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id_estudiante"));
                    s.setDocumentType(rs.getString("tipo_documento"));
                    s.setDocumentNumber(rs.getString("numero_documento"));
                    s.setFirstName(rs.getString("primer_nombre"));
                    s.setSecondName(rs.getString("segundo_nombre"));
                    s.setFirstLastName(rs.getString("primer_apellido"));
                    s.setSecondLastName(rs.getString("segundo_apellido"));
                    Date b = rs.getDate("fecha_nacimiento");
                    if (b != null) s.setBirthDate(b.toLocalDate());
                    s.setGender(rs.getString("genero"));
                    s.setStatus(rs.getString("estado"));
                    students.add(s);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching students for guardian: " + guardianId, e);
        }
        return students;
    }

    private Guardian mapResultSetToGuardian(ResultSet rs) throws SQLException {
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
        return g;
    }
}
