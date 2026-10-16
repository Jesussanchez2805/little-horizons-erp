package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Invoice;
import com.littlehorizons.erp.model.Payment;
import com.littlehorizons.erp.model.PaymentConcept;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of FinancialDAO.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class FinancialDAOImpl implements FinancialDAO {

    @Override
    public List<PaymentConcept> findAllConcepts() {
        List<PaymentConcept> list = new ArrayList<>();
        String sql = "SELECT * FROM conceptos_pago WHERE esta_activo = TRUE ORDER BY id_concepto";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToConcept(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching payment concepts", e);
        }
        return list;
    }

    @Override
    public Optional<PaymentConcept> findConceptById(int id) {
        String sql = "SELECT * FROM conceptos_pago WHERE id_concepto = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToConcept(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding concept ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PaymentConcept> findConceptByCode(String code) {
        String sql = "SELECT * FROM conceptos_pago WHERE codigo_concepto = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToConcept(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding concept code: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public Payment recordPayment(Payment p) {
        String sql = "INSERT INTO pagos (id_estudiante, id_acudiente, id_concepto, monto_pagado, " +
                     "fecha_pago, metodo_pago, numero_recibo, año_academico, periodo_academico, notas) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getStudentId());
            if (p.getGuardianId() != null) {
                stmt.setInt(2, p.getGuardianId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, p.getConceptId());
            stmt.setBigDecimal(4, p.getAmountPaid());
            stmt.setDate(5, Date.valueOf(p.getPaymentDate()));
            stmt.setString(6, p.getPaymentMethod());
            stmt.setString(7, p.getReceiptNumber());
            if (p.getAcademicYear() != null) {
                stmt.setInt(8, p.getAcademicYear());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.setString(9, p.getAcademicPeriod());
            stmt.setString(10, p.getNotes());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Payment recorded: " + p.getReceiptNumber() + " - $" + p.getAmountPaid());
                return p;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error recording payment for student: " + p.getStudentId(), e);
        }
        return null;
    }

    @Override
    public Optional<Payment> findPaymentById(int id) {
        String sql = "SELECT p.*, cp.nombre_concepto, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente, " +
                     "a.numero_documento AS doc_acudiente " +
                     "FROM pagos p " +
                     "INNER JOIN conceptos_pago cp ON p.id_concepto = cp.id_concepto " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "WHERE p.id_pago = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding payment ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Payment> findPaymentByReceipt(String receiptNumber) {
        String sql = "SELECT p.*, cp.nombre_concepto, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente, " +
                     "a.numero_documento AS doc_acudiente " +
                     "FROM pagos p " +
                     "INNER JOIN conceptos_pago cp ON p.id_concepto = cp.id_concepto " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "WHERE p.numero_recibo = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, receiptNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding payment by receipt: " + receiptNumber, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findPaymentsByStudent(int studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, cp.nombre_concepto, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente, " +
                     "a.numero_documento AS doc_acudiente " +
                     "FROM pagos p " +
                     "INNER JOIN conceptos_pago cp ON p.id_concepto = cp.id_concepto " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "WHERE p.id_estudiante = ? ORDER BY p.fecha_pago DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding payments for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public List<Payment> findPaymentsByGuardian(int guardianId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, cp.nombre_concepto, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente, " +
                     "a.numero_documento AS doc_acudiente " +
                     "FROM pagos p " +
                     "INNER JOIN conceptos_pago cp ON p.id_concepto = cp.id_concepto " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "WHERE p.id_acudiente = ? ORDER BY p.fecha_pago DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guardianId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding payments for guardian: " + guardianId, e);
        }
        return list;
    }

    @Override
    public List<Payment> findAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, cp.nombre_concepto, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente, " +
                     "a.numero_documento AS doc_acudiente " +
                     "FROM pagos p " +
                     "INNER JOIN conceptos_pago cp ON p.id_concepto = cp.id_concepto " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "ORDER BY p.fecha_pago DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all payments", e);
        }
        return list;
    }

    @Override
    public String generateNextReceiptNumber() {
        int year = LocalDate.now().getYear();
        String sql = "SELECT MAX(id_pago) AS max_id FROM pagos";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int next = 1;
            if (rs.next()) {
                next = rs.getInt("max_id") + 1;
            }
            return String.format("REC-%d-%04d", year, next);
        } catch (SQLException e) {
            AppLogger.warning("Error generating next receipt number, defaulting to timestamp: " + e.getMessage());
            return "REC-" + year + "-" + System.currentTimeMillis() % 10000;
        }
    }

    @Override
    public List<Student> findDefaulterStudents(int year, String period) {
        List<Student> list = new ArrayList<>();
        // Finds active students enrolled in year who do not have a tuition payment for that period
        String sql = "SELECT e.* FROM estudiantes e " +
                     "INNER JOIN matriculas m ON e.id_estudiante = m.id_estudiante " +
                     "WHERE m.año_academico = ? AND m.estado_matricula = 'Activa' " +
                     "AND e.id_estudiante NOT IN (" +
                     "    SELECT p.id_estudiante FROM pagos p " +
                     "    INNER JOIN conceptos_pago cp ON p.id_concepto = cp.id_concepto " +
                     "    WHERE p.año_academico = ? AND p.periodo_academico = ? " +
                     "    AND (cp.codigo_concepto = 'PEN-MEN' OR cp.codigo_concepto = 'PENSION')" +
                     ") ORDER BY e.primer_apellido, e.primer_nombre";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);
            stmt.setInt(2, year);
            stmt.setString(3, period);

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
                    s.setEmail(rs.getString("correo_electronico"));
                    s.setPhone(rs.getString("telefono"));
                    s.setStatus(rs.getString("estado"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error identifying defaulters for " + year + " period " + period, e);
        }
        return list;
    }

    @Override
    public Invoice createInvoice(Invoice inv) {
        String sql = "INSERT INTO facturas (numero_factura, id_pago, fecha_emision, cufe, " +
                     "nombre_archivo_pdf, ruta_archivo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, inv.getInvoiceNumber());
            stmt.setInt(2, inv.getPaymentId());
            stmt.setDate(3, Date.valueOf(inv.getIssueDate()));
            stmt.setString(4, inv.getCufe());
            stmt.setString(5, inv.getPdfFileName());
            stmt.setString(6, inv.getFilePath());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        inv.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Invoice created: " + inv.getInvoiceNumber());
                return inv;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error creating invoice: " + inv.getInvoiceNumber(), e);
        }
        return null;
    }

    @Override
    public Optional<Invoice> findInvoiceById(int id) {
        String sql = "SELECT f.*, p.monto_pagado, p.numero_recibo, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente " +
                     "FROM facturas f " +
                     "INNER JOIN pagos p ON f.id_pago = p.id_pago " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "WHERE f.id_factura = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding invoice ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findInvoiceByPaymentId(int paymentId) {
        String sql = "SELECT f.*, p.monto_pagado, p.numero_recibo, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente " +
                     "FROM facturas f " +
                     "INNER JOIN pagos p ON f.id_pago = p.id_pago " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "WHERE f.id_pago = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvoice(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding invoice by payment ID: " + paymentId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Invoice> findAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT f.*, p.monto_pagado, p.numero_recibo, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "CONCAT(a.primer_nombre, ' ', a.primer_apellido) AS nom_acudiente " +
                     "FROM facturas f " +
                     "INNER JOIN pagos p ON f.id_pago = p.id_pago " +
                     "INNER JOIN estudiantes e ON p.id_estudiante = e.id_estudiante " +
                     "LEFT JOIN acudientes a ON p.id_acudiente = a.id_acudiente " +
                     "ORDER BY f.fecha_emision DESC, f.id_factura DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all invoices", e);
        }
        return list;
    }

    @Override
    public String generateNextInvoiceNumber() {
        String sql = "SELECT MAX(id_factura) AS max_id FROM facturas";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int next = 1;
            if (rs.next()) {
                next = rs.getInt("max_id") + 1;
            }
            return String.format("FE-LH-%06d", next);
        } catch (SQLException e) {
            AppLogger.warning("Error generating next invoice number: " + e.getMessage());
            return "FE-LH-" + String.format("%06d", System.currentTimeMillis() % 1000000);
        }
    }

    private PaymentConcept mapResultSetToConcept(ResultSet rs) throws SQLException {
        PaymentConcept c = new PaymentConcept();
        c.setId(rs.getInt("id_concepto"));
        c.setCode(rs.getString("codigo_concepto"));
        c.setName(rs.getString("nombre_concepto"));
        c.setDescription(rs.getString("descripcion"));
        c.setBaseAmount(rs.getBigDecimal("monto_base"));
        c.setRecurring(rs.getBoolean("es_recurrente"));
        c.setActive(rs.getBoolean("esta_activo"));
        return c;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id_pago"));
        p.setStudentId(rs.getInt("id_estudiante"));
        int ac = rs.getInt("id_acudiente");
        if (!rs.wasNull()) p.setGuardianId(ac);
        p.setConceptId(rs.getInt("id_concepto"));
        p.setAmountPaid(rs.getBigDecimal("monto_pagado"));
        p.setPaymentDate(rs.getDate("fecha_pago").toLocalDate());
        p.setPaymentMethod(rs.getString("metodo_pago"));
        p.setReceiptNumber(rs.getString("numero_recibo"));
        int yr = rs.getInt("año_academico");
        if (!rs.wasNull()) p.setAcademicYear(yr);
        p.setAcademicPeriod(rs.getString("periodo_academico"));
        p.setNotes(rs.getString("notas"));
        p.setConceptName(rs.getString("nombre_concepto"));
        p.setStudentName(rs.getString("nom_estudiante"));
        p.setGuardianName(rs.getString("nom_acudiente"));
        p.setGuardianDocNumber(rs.getString("doc_acudiente"));
        return p;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setId(rs.getInt("id_factura"));
        inv.setInvoiceNumber(rs.getString("numero_factura"));
        inv.setPaymentId(rs.getInt("id_pago"));
        inv.setIssueDate(rs.getDate("fecha_emision").toLocalDate());
        inv.setCufe(rs.getString("cufe"));
        inv.setPdfFileName(rs.getString("nombre_archivo_pdf"));
        inv.setFilePath(rs.getString("ruta_archivo"));
        inv.setStudentName(rs.getString("nom_estudiante"));
        inv.setGuardianName(rs.getString("nom_acudiente"));
        inv.setReceiptNumber(rs.getString("numero_recibo"));
        inv.setAmount(rs.getBigDecimal("monto_pagado"));
        return inv;
    }
}
