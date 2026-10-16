package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.ResourceCategory;
import com.littlehorizons.erp.model.ResourceItem;
import com.littlehorizons.erp.model.StudentLoan;
import com.littlehorizons.erp.model.TeacherLoan;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ResourceDAO.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ResourceDAOImpl implements ResourceDAO {

    @Override
    public List<ResourceCategory> findAllCategories() {
        List<ResourceCategory> list = new ArrayList<>();
        String sql = "SELECT * FROM categorias_recurso ORDER BY id_categoria";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ResourceCategory(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre_categoria"),
                        rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching resource categories", e);
        }
        return list;
    }

    @Override
    public ResourceItem createResource(ResourceItem r) {
        String sql = "INSERT INTO recursos (codigo_recurso, nombre_recurso, id_categoria, descripcion, " +
                     "fecha_adquisicion, ubicacion_fisica, estado_recurso) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, r.getCode());
            stmt.setString(2, r.getName());
            stmt.setInt(3, r.getCategoryId());
            stmt.setString(4, r.getDescription());
            if (r.getAcquisitionDate() != null) {
                stmt.setDate(5, Date.valueOf(r.getAcquisitionDate()));
            } else {
                stmt.setDate(5, Date.valueOf(LocalDate.now()));
            }
            stmt.setString(6, r.getPhysicalLocation());
            stmt.setString(7, r.getStatus() != null ? r.getStatus() : "Disponible");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        r.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Resource created: " + r.getCode() + " - " + r.getName());
                return r;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error inserting resource: " + r.getName(), e);
        }
        return null;
    }

    @Override
    public Optional<ResourceItem> findResourceById(int id) {
        String sql = "SELECT r.*, cr.nombre_categoria FROM recursos r " +
                     "INNER JOIN categorias_recurso cr ON r.id_categoria = cr.id_categoria WHERE r.id_recurso = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResource(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding resource ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceItem> findResourceByCode(String code) {
        String sql = "SELECT r.*, cr.nombre_categoria FROM recursos r " +
                     "INNER JOIN categorias_recurso cr ON r.id_categoria = cr.id_categoria WHERE r.codigo_recurso = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResource(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding resource code: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public List<ResourceItem> findAllResources() {
        List<ResourceItem> list = new ArrayList<>();
        String sql = "SELECT r.*, cr.nombre_categoria FROM recursos r " +
                     "INNER JOIN categorias_recurso cr ON r.id_categoria = cr.id_categoria ORDER BY r.nombre_recurso";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToResource(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all resources", e);
        }
        return list;
    }

    @Override
    public List<ResourceItem> findResourcesByCategory(int categoryId) {
        List<ResourceItem> list = new ArrayList<>();
        String sql = "SELECT r.*, cr.nombre_categoria FROM recursos r " +
                     "INNER JOIN categorias_recurso cr ON r.id_categoria = cr.id_categoria " +
                     "WHERE r.id_categoria = ? ORDER BY r.nombre_recurso";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToResource(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding resources for category: " + categoryId, e);
        }
        return list;
    }

    @Override
    public boolean updateResourceStatus(int resourceId, String status) {
        String sql = "UPDATE recursos SET estado_recurso = ? WHERE id_recurso = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, resourceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error updating status for resource: " + resourceId, e);
            return false;
        }
    }

    @Override
    public StudentLoan createStudentLoan(StudentLoan loan) {
        String sql = "INSERT INTO prestamos_estudiante (id_recurso, id_estudiante, fecha_prestamo, " +
                     "fecha_devolucion_esperada, estado_prestamo, observaciones) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getResourceId());
            stmt.setInt(2, loan.getStudentId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            stmt.setString(5, loan.getStatus() != null ? loan.getStatus() : "Activo");
            stmt.setString(6, loan.getObservations());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        loan.setId(rs.getInt(1));
                    }
                }
                // Automatically set resource status to 'Prestado'
                updateResourceStatus(loan.getResourceId(), "Prestado");
                AppLogger.info("Student loan created ID: " + loan.getId() + " for resource: " + loan.getResourceId());
                return loan;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error creating student loan for resource: " + loan.getResourceId(), e);
        }
        return null;
    }

    @Override
    public Optional<StudentLoan> findStudentLoanById(int id) {
        String sql = "SELECT pl.*, r.codigo_recurso, r.nombre_recurso, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, e.numero_documento " +
                     "FROM prestamos_estudiante pl " +
                     "INNER JOIN recursos r ON pl.id_recurso = r.id_recurso " +
                     "INNER JOIN estudiantes e ON pl.id_estudiante = e.id_estudiante " +
                     "WHERE pl.id_prestamo = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudentLoan(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding student loan ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<StudentLoan> findActiveStudentLoans() {
        List<StudentLoan> list = new ArrayList<>();
        String sql = "SELECT pl.*, r.codigo_recurso, r.nombre_recurso, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, e.numero_documento " +
                     "FROM prestamos_estudiante pl " +
                     "INNER JOIN recursos r ON pl.id_recurso = r.id_recurso " +
                     "INNER JOIN estudiantes e ON pl.id_estudiante = e.id_estudiante " +
                     "WHERE pl.estado_prestamo IN ('Activo', 'Vencido') ORDER BY pl.fecha_devolucion_esperada ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStudentLoan(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching active student loans", e);
        }
        return list;
    }

    @Override
    public List<StudentLoan> findOverdueStudentLoans() {
        List<StudentLoan> list = new ArrayList<>();
        String sql = "SELECT pl.*, r.codigo_recurso, r.nombre_recurso, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, e.numero_documento " +
                     "FROM prestamos_estudiante pl " +
                     "INNER JOIN recursos r ON pl.id_recurso = r.id_recurso " +
                     "INNER JOIN estudiantes e ON pl.id_estudiante = e.id_estudiante " +
                     "WHERE pl.estado_prestamo = 'Activo' AND pl.fecha_devolucion_esperada < CURRENT_DATE() " +
                     "ORDER BY pl.fecha_devolucion_esperada ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStudentLoan(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching overdue student loans", e);
        }
        return list;
    }

    @Override
    public boolean returnStudentLoan(int loanId, LocalDate returnDate, String observations) {
        String selectSql = "SELECT id_recurso FROM prestamos_estudiante WHERE id_prestamo = ?";
        String updateSql = "UPDATE prestamos_estudiante SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ?, " +
                           "observaciones = CONCAT(IFNULL(observaciones, ''), ' [Devuelto: ', ?, ']') WHERE id_prestamo = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            int resourceId = 0;
            try (PreparedStatement sStmt = conn.prepareStatement(selectSql)) {
                sStmt.setInt(1, loanId);
                try (ResultSet rs = sStmt.executeQuery()) {
                    if (rs.next()) {
                        resourceId = rs.getInt("id_recurso");
                    }
                }
            }

            try (PreparedStatement uStmt = conn.prepareStatement(updateSql)) {
                uStmt.setDate(1, Date.valueOf(returnDate));
                uStmt.setString(2, observations != null ? observations : "Sin observaciones");
                uStmt.setInt(3, loanId);
                int rows = uStmt.executeUpdate();
                if (rows > 0 && resourceId > 0) {
                    updateResourceStatus(resourceId, "Disponible");
                    AppLogger.info("Student loan " + loanId + " returned. Resource " + resourceId + " set to Disponible.");
                    return true;
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error returning student loan: " + loanId, e);
        }
        return false;
    }

    @Override
    public TeacherLoan createTeacherLoan(TeacherLoan loan) {
        String sql = "INSERT INTO prestamos_profesor (id_recurso, id_profesor, fecha_prestamo, " +
                     "fecha_devolucion_esperada, estado_prestamo, observaciones) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, loan.getResourceId());
            stmt.setInt(2, loan.getTeacherId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            stmt.setString(5, loan.getStatus() != null ? loan.getStatus() : "Activo");
            stmt.setString(6, loan.getObservations());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        loan.setId(rs.getInt(1));
                    }
                }
                updateResourceStatus(loan.getResourceId(), "Prestado");
                AppLogger.info("Teacher loan created ID: " + loan.getId() + " for resource: " + loan.getResourceId());
                return loan;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error creating teacher loan for resource: " + loan.getResourceId(), e);
        }
        return null;
    }

    @Override
    public Optional<TeacherLoan> findTeacherLoanById(int id) {
        String sql = "SELECT pl.*, r.codigo_recurso, r.nombre_recurso, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, p.correo_electronico " +
                     "FROM prestamos_profesor pl " +
                     "INNER JOIN recursos r ON pl.id_recurso = r.id_recurso " +
                     "INNER JOIN profesores p ON pl.id_profesor = p.id_profesor " +
                     "WHERE pl.id_prestamo = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacherLoan(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding teacher loan ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<TeacherLoan> findActiveTeacherLoans() {
        List<TeacherLoan> list = new ArrayList<>();
        String sql = "SELECT pl.*, r.codigo_recurso, r.nombre_recurso, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, p.correo_electronico " +
                     "FROM prestamos_profesor pl " +
                     "INNER JOIN recursos r ON pl.id_recurso = r.id_recurso " +
                     "INNER JOIN profesores p ON pl.id_profesor = p.id_profesor " +
                     "WHERE pl.estado_prestamo IN ('Activo', 'Vencido') ORDER BY pl.fecha_devolucion_esperada ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTeacherLoan(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching active teacher loans", e);
        }
        return list;
    }

    @Override
    public List<TeacherLoan> findOverdueTeacherLoans() {
        List<TeacherLoan> list = new ArrayList<>();
        String sql = "SELECT pl.*, r.codigo_recurso, r.nombre_recurso, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, p.correo_electronico " +
                     "FROM prestamos_profesor pl " +
                     "INNER JOIN recursos r ON pl.id_recurso = r.id_recurso " +
                     "INNER JOIN profesores p ON pl.id_profesor = p.id_profesor " +
                     "WHERE pl.estado_prestamo = 'Activo' AND pl.fecha_devolucion_esperada < CURRENT_DATE() " +
                     "ORDER BY pl.fecha_devolucion_esperada ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToTeacherLoan(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching overdue teacher loans", e);
        }
        return list;
    }

    @Override
    public boolean returnTeacherLoan(int loanId, LocalDate returnDate, String observations) {
        String selectSql = "SELECT id_recurso FROM prestamos_profesor WHERE id_prestamo = ?";
        String updateSql = "UPDATE prestamos_profesor SET estado_prestamo = 'Devuelto', fecha_devolucion_real = ?, " +
                           "observaciones = CONCAT(IFNULL(observaciones, ''), ' [Devuelto: ', ?, ']') WHERE id_prestamo = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            int resourceId = 0;
            try (PreparedStatement sStmt = conn.prepareStatement(selectSql)) {
                sStmt.setInt(1, loanId);
                try (ResultSet rs = sStmt.executeQuery()) {
                    if (rs.next()) {
                        resourceId = rs.getInt("id_recurso");
                    }
                }
            }

            try (PreparedStatement uStmt = conn.prepareStatement(updateSql)) {
                uStmt.setDate(1, Date.valueOf(returnDate));
                uStmt.setString(2, observations != null ? observations : "Sin observaciones");
                uStmt.setInt(3, loanId);
                int rows = uStmt.executeUpdate();
                if (rows > 0 && resourceId > 0) {
                    updateResourceStatus(resourceId, "Disponible");
                    AppLogger.info("Teacher loan " + loanId + " returned. Resource " + resourceId + " set to Disponible.");
                    return true;
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error returning teacher loan: " + loanId, e);
        }
        return false;
    }

    private ResourceItem mapResultSetToResource(ResultSet rs) throws SQLException {
        ResourceItem r = new ResourceItem();
        r.setId(rs.getInt("id_recurso"));
        r.setCode(rs.getString("codigo_recurso"));
        r.setName(rs.getString("nombre_recurso"));
        r.setCategoryId(rs.getInt("id_categoria"));
        r.setDescription(rs.getString("descripcion"));
        Date acq = rs.getDate("fecha_adquisicion");
        if (acq != null) r.setAcquisitionDate(acq.toLocalDate());
        r.setPhysicalLocation(rs.getString("ubicacion_fisica"));
        r.setStatus(rs.getString("estado_recurso"));
        r.setCategoryName(rs.getString("nombre_categoria"));
        return r;
    }

    private StudentLoan mapResultSetToStudentLoan(ResultSet rs) throws SQLException {
        StudentLoan sl = new StudentLoan();
        sl.setId(rs.getInt("id_prestamo"));
        sl.setResourceId(rs.getInt("id_recurso"));
        sl.setStudentId(rs.getInt("id_estudiante"));
        sl.setLoanDate(rs.getDate("fecha_prestamo").toLocalDate());
        sl.setExpectedReturnDate(rs.getDate("fecha_devolucion_esperada").toLocalDate());
        Date ret = rs.getDate("fecha_devolucion_real");
        if (ret != null) sl.setActualReturnDate(ret.toLocalDate());
        sl.setStatus(rs.getString("estado_prestamo"));
        sl.setObservations(rs.getString("observaciones"));
        sl.setResourceCode(rs.getString("codigo_recurso"));
        sl.setResourceName(rs.getString("nombre_recurso"));
        sl.setStudentName(rs.getString("nom_estudiante"));
        sl.setStudentDocNumber(rs.getString("numero_documento"));
        return sl;
    }

    private TeacherLoan mapResultSetToTeacherLoan(ResultSet rs) throws SQLException {
        TeacherLoan tl = new TeacherLoan();
        tl.setId(rs.getInt("id_prestamo"));
        tl.setResourceId(rs.getInt("id_recurso"));
        tl.setTeacherId(rs.getInt("id_profesor"));
        tl.setLoanDate(rs.getDate("fecha_prestamo").toLocalDate());
        tl.setExpectedReturnDate(rs.getDate("fecha_devolucion_esperada").toLocalDate());
        Date ret = rs.getDate("fecha_devolucion_real");
        if (ret != null) tl.setActualReturnDate(ret.toLocalDate());
        tl.setStatus(rs.getString("estado_prestamo"));
        tl.setObservations(rs.getString("observaciones"));
        tl.setResourceCode(rs.getString("codigo_recurso"));
        tl.setResourceName(rs.getString("nombre_recurso"));
        tl.setTeacherName(rs.getString("nom_profesor"));
        tl.setTeacherEmail(rs.getString("correo_electronico"));
        return tl;
    }
}
