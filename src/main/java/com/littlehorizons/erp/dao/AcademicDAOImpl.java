package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.*;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of AcademicDAO.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class AcademicDAOImpl implements AcademicDAO {

    @Override
    public List<GradeLevel> findAllGradeLevels() {
        List<GradeLevel> list = new ArrayList<>();
        String sql = "SELECT * FROM grados ORDER BY nivel_numerico ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new GradeLevel(
                        rs.getInt("id_grado"),
                        rs.getString("nombre_grado"),
                        rs.getInt("nivel_numerico")
                ));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all grade levels", e);
        }
        return list;
    }

    @Override
    public Optional<GradeLevel> findGradeLevelById(int id) {
        String sql = "SELECT * FROM grados WHERE id_grado = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new GradeLevel(
                            rs.getInt("id_grado"),
                            rs.getString("nombre_grado"),
                            rs.getInt("nivel_numerico")
                    ));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding grade level ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Enrollment enrollStudent(Enrollment enrollment) {
        String sql = "INSERT INTO matriculas (id_estudiante, id_grado, año_academico, estado_matricula, " +
                     "fecha_matricula, fecha_retiro, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getGradeId());
            stmt.setInt(3, enrollment.getAcademicYear());
            stmt.setString(4, enrollment.getStatus() != null ? enrollment.getStatus() : "Activa");
            stmt.setDate(5, Date.valueOf(enrollment.getEnrollmentDate()));
            if (enrollment.getWithdrawalDate() != null) {
                stmt.setDate(6, Date.valueOf(enrollment.getWithdrawalDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.setString(7, enrollment.getObservations());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        enrollment.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Student " + enrollment.getStudentId() + " enrolled in grade " + enrollment.getGradeId());
                return enrollment;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error creating enrollment for student ID: " + enrollment.getStudentId(), e);
        }
        return null;
    }

    @Override
    public Optional<Enrollment> findEnrollmentByStudentAndYear(int studentId, int academicYear) {
        String sql = "SELECT m.*, g.nombre_grado, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "e.numero_documento " +
                     "FROM matriculas m " +
                     "INNER JOIN grados g ON m.id_grado = g.id_grado " +
                     "INNER JOIN estudiantes e ON m.id_estudiante = e.id_estudiante " +
                     "WHERE m.id_estudiante = ? AND m.año_academico = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, academicYear);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding enrollment for student: " + studentId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Enrollment> findEnrollmentsByGradeAndYear(int gradeId, int academicYear) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT m.*, g.nombre_grado, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, " +
                     "e.numero_documento " +
                     "FROM matriculas m " +
                     "INNER JOIN grados g ON m.id_grado = g.id_grado " +
                     "INNER JOIN estudiantes e ON m.id_estudiante = e.id_estudiante " +
                     "WHERE m.id_grado = ? AND m.año_academico = ? ORDER BY nom_estudiante";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gradeId);
            stmt.setInt(2, academicYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching enrollments for grade: " + gradeId, e);
        }
        return list;
    }

    @Override
    public Course createCourse(Course course) {
        String sql = "INSERT INTO materias (codigo_materia, nombre_materia, descripcion, id_grado, horas_semanales, esta_activa) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getGradeId());
            stmt.setInt(5, course.getWeeklyHours());
            stmt.setBoolean(6, course.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        course.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Course created: " + course.getName());
                return course;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error inserting course: " + course.getName(), e);
        }
        return null;
    }

    @Override
    public Optional<Course> findCourseById(int courseId) {
        String sql = "SELECT m.*, g.nombre_grado FROM materias m " +
                     "INNER JOIN grados g ON m.id_grado = g.id_grado WHERE m.id_materia = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding course ID: " + courseId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Course> findCourseByCode(String code) {
        String sql = "SELECT m.*, g.nombre_grado FROM materias m " +
                     "INNER JOIN grados g ON m.id_grado = g.id_grado WHERE m.codigo_materia = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding course by code: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Course> findAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT m.*, g.nombre_grado FROM materias m " +
                     "INNER JOIN grados g ON m.id_grado = g.id_grado ORDER BY g.nivel_numerico, m.nombre_materia";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all courses", e);
        }
        return list;
    }

    @Override
    public List<Course> findCoursesByGrade(int gradeId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT m.*, g.nombre_grado FROM materias m " +
                     "INNER JOIN grados g ON m.id_grado = g.id_grado WHERE m.id_grado = ? AND m.esta_activa = TRUE";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gradeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching courses for grade: " + gradeId, e);
        }
        return list;
    }

    @Override
    public CourseRegistration registerStudentInCourse(CourseRegistration reg) {
        String sql = "INSERT INTO inscripciones_materia (id_estudiante, id_materia, año_academico, " +
                     "periodo_academico, seccion, fecha_inscripcion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reg.getStudentId());
            stmt.setInt(2, reg.getCourseId());
            stmt.setInt(3, reg.getAcademicYear());
            stmt.setString(4, reg.getAcademicPeriod());
            stmt.setString(5, reg.getSection() != null ? reg.getSection() : "A");
            stmt.setDate(6, Date.valueOf(reg.getRegistrationDate()));
            stmt.setString(7, reg.getStatus() != null ? reg.getStatus() : "Activa");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reg.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Student " + reg.getStudentId() + " registered in course " + reg.getCourseId());
                return reg;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error registering student in course", e);
        }
        return null;
    }

    @Override
    public List<CourseRegistration> findRegistrationsByStudent(int studentId, int academicYear, String period) {
        List<CourseRegistration> list = new ArrayList<>();
        String sql = "SELECT im.*, m.nombre_materia, m.codigo_materia, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM inscripciones_materia im " +
                     "INNER JOIN materias m ON im.id_materia = m.id_materia " +
                     "INNER JOIN estudiantes e ON im.id_estudiante = e.id_estudiante " +
                     "WHERE im.id_estudiante = ? AND im.año_academico = ? AND im.periodo_academico = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, academicYear);
            stmt.setString(3, period);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CourseRegistration cr = new CourseRegistration();
                    cr.setId(rs.getInt("id_inscripcion"));
                    cr.setStudentId(rs.getInt("id_estudiante"));
                    cr.setCourseId(rs.getInt("id_materia"));
                    cr.setAcademicYear(rs.getInt("año_academico"));
                    cr.setAcademicPeriod(rs.getString("periodo_academico"));
                    cr.setSection(rs.getString("seccion"));
                    cr.setRegistrationDate(rs.getDate("fecha_inscripcion").toLocalDate());
                    cr.setStatus(rs.getString("estado"));
                    cr.setCourseName(rs.getString("nombre_materia"));
                    cr.setCourseCode(rs.getString("codigo_materia"));
                    cr.setStudentName(rs.getString("nom_estudiante"));
                    list.add(cr);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding course registrations for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public AcademicRecord recordGrade(AcademicRecord record) {
        String sql = "INSERT INTO registros_academicos (id_estudiante, id_materia, id_profesor, año_academico, " +
                     "periodo_academico, valor_nota, observaciones, fecha_registro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE valor_nota = VALUES(valor_nota), observaciones = VALUES(observaciones), " +
                     "fecha_registro = VALUES(fecha_registro)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, record.getStudentId());
            stmt.setInt(2, record.getCourseId());
            stmt.setInt(3, record.getTeacherId());
            stmt.setInt(4, record.getAcademicYear());
            stmt.setString(5, record.getAcademicPeriod());
            stmt.setBigDecimal(6, record.getGradeValue());
            stmt.setString(7, record.getObservations());
            stmt.setDate(8, Date.valueOf(record.getRecordDate()));

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        record.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Grade saved: Student " + record.getStudentId() + ", Course " + record.getCourseId() + " -> " + record.getGradeValue());
                return record;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error recording grade for student: " + record.getStudentId(), e);
        }
        return null;
    }

    @Override
    public List<AcademicRecord> findGradesByStudent(int studentId) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT ra.*, m.nombre_materia, m.codigo_materia, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, e.numero_documento " +
                     "FROM registros_academicos ra " +
                     "INNER JOIN materias m ON ra.id_materia = m.id_materia " +
                     "INNER JOIN profesores p ON ra.id_profesor = p.id_profesor " +
                     "INNER JOIN estudiantes e ON ra.id_estudiante = e.id_estudiante " +
                     "WHERE ra.id_estudiante = ? ORDER BY ra.año_academico DESC, ra.periodo_academico, m.nombre_materia";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAcademicRecord(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching grades for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public List<AcademicRecord> findGradesByStudentAndCourse(int studentId, int courseId) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT ra.*, m.nombre_materia, m.codigo_materia, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, e.numero_documento " +
                     "FROM registros_academicos ra " +
                     "INNER JOIN materias m ON ra.id_materia = m.id_materia " +
                     "INNER JOIN profesores p ON ra.id_profesor = p.id_profesor " +
                     "INNER JOIN estudiantes e ON ra.id_estudiante = e.id_estudiante " +
                     "WHERE ra.id_estudiante = ? AND ra.id_materia = ? ORDER BY ra.periodo_academico";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAcademicRecord(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching grades for student " + studentId + " in course " + courseId, e);
        }
        return list;
    }

    @Override
    public List<AcademicRecord> findGradesByCourseAndPeriod(int courseId, int academicYear, String period) {
        List<AcademicRecord> list = new ArrayList<>();
        String sql = "SELECT ra.*, m.nombre_materia, m.codigo_materia, " +
                     "CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante, e.numero_documento " +
                     "FROM registros_academicos ra " +
                     "INNER JOIN materias m ON ra.id_materia = m.id_materia " +
                     "INNER JOIN profesores p ON ra.id_profesor = p.id_profesor " +
                     "INNER JOIN estudiantes e ON ra.id_estudiante = e.id_estudiante " +
                     "WHERE ra.id_materia = ? AND ra.año_academico = ? AND ra.periodo_academico = ? " +
                     "ORDER BY nom_estudiante";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setInt(2, academicYear);
            stmt.setString(3, period);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAcademicRecord(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching course grades for course " + courseId, e);
        }
        return list;
    }

    @Override
    public Attendance recordAttendance(Attendance att) {
        String sql = "INSERT INTO asistencias (id_estudiante, id_materia, fecha_asistencia, estado_asistencia, " +
                     "observaciones, registrado_por) VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE estado_asistencia = VALUES(estado_asistencia), observaciones = VALUES(observaciones)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, att.getStudentId());
            stmt.setInt(2, att.getCourseId());
            stmt.setDate(3, Date.valueOf(att.getAttendanceDate()));
            stmt.setString(4, att.getStatus());
            stmt.setString(5, att.getObservations());
            if (att.getRegisteredByTeacherId() != null) {
                stmt.setInt(6, att.getRegisteredByTeacherId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        att.setId(rs.getInt(1));
                    }
                }
                return att;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error recording attendance for student: " + att.getStudentId(), e);
        }
        return null;
    }

    @Override
    public List<Attendance> findAttendanceByStudentAndCourse(int studentId, int courseId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, m.nombre_materia, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM asistencias a " +
                     "INNER JOIN materias m ON a.id_materia = m.id_materia " +
                     "INNER JOIN estudiantes e ON a.id_estudiante = e.id_estudiante " +
                     "WHERE a.id_estudiante = ? AND a.id_materia = ? ORDER BY a.fecha_asistencia DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = new Attendance();
                    a.setId(rs.getInt("id_asistencia"));
                    a.setStudentId(rs.getInt("id_estudiante"));
                    a.setCourseId(rs.getInt("id_materia"));
                    a.setAttendanceDate(rs.getDate("fecha_asistencia").toLocalDate());
                    a.setStatus(rs.getString("estado_asistencia"));
                    a.setObservations(rs.getString("observaciones"));
                    a.setCourseName(rs.getString("nombre_materia"));
                    a.setStudentName(rs.getString("nom_estudiante"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching attendance for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public List<Attendance> findAbsencesByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, m.nombre_materia, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM asistencias a " +
                     "INNER JOIN materias m ON a.id_materia = m.id_materia " +
                     "INNER JOIN estudiantes e ON a.id_estudiante = e.id_estudiante " +
                     "WHERE a.id_estudiante = ? AND a.estado_asistencia IN ('Ausente', 'Tardanza') " +
                     "ORDER BY a.fecha_asistencia DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = new Attendance();
                    a.setId(rs.getInt("id_asistencia"));
                    a.setStudentId(rs.getInt("id_estudiante"));
                    a.setCourseId(rs.getInt("id_materia"));
                    a.setAttendanceDate(rs.getDate("fecha_asistencia").toLocalDate());
                    a.setStatus(rs.getString("estado_asistencia"));
                    a.setObservations(rs.getString("observaciones"));
                    a.setCourseName(rs.getString("nombre_materia"));
                    a.setStudentName(rs.getString("nom_estudiante"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching absences for student: " + studentId, e);
        }
        return list;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id_materia"));
        c.setCode(rs.getString("codigo_materia"));
        c.setName(rs.getString("nombre_materia"));
        c.setDescription(rs.getString("descripcion"));
        c.setGradeId(rs.getInt("id_grado"));
        c.setWeeklyHours(rs.getInt("horas_semanales"));
        c.setActive(rs.getBoolean("esta_activa"));
        c.setGradeName(rs.getString("nombre_grado"));
        return c;
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment en = new Enrollment();
        en.setId(rs.getInt("id_matricula"));
        en.setStudentId(rs.getInt("id_estudiante"));
        en.setGradeId(rs.getInt("id_grado"));
        en.setAcademicYear(rs.getInt("año_academico"));
        en.setStatus(rs.getString("estado_matricula"));
        en.setEnrollmentDate(rs.getDate("fecha_matricula").toLocalDate());
        Date ret = rs.getDate("fecha_retiro");
        if (ret != null) en.setWithdrawalDate(ret.toLocalDate());
        en.setObservations(rs.getString("observaciones"));
        en.setGradeName(rs.getString("nombre_grado"));
        en.setStudentName(rs.getString("nom_estudiante"));
        en.setStudentDocNumber(rs.getString("numero_documento"));
        return en;
    }

    private AcademicRecord mapResultSetToAcademicRecord(ResultSet rs) throws SQLException {
        AcademicRecord ar = new AcademicRecord();
        ar.setId(rs.getInt("id_registro"));
        ar.setStudentId(rs.getInt("id_estudiante"));
        ar.setCourseId(rs.getInt("id_materia"));
        ar.setTeacherId(rs.getInt("id_profesor"));
        ar.setAcademicYear(rs.getInt("año_academico"));
        ar.setAcademicPeriod(rs.getString("periodo_academico"));
        ar.setGradeValue(rs.getBigDecimal("valor_nota"));
        ar.setObservations(rs.getString("observaciones"));
        ar.setRecordDate(rs.getDate("fecha_registro").toLocalDate());
        ar.setCourseName(rs.getString("nombre_materia"));
        ar.setCourseCode(rs.getString("codigo_materia"));
        ar.setTeacherName(rs.getString("nom_profesor"));
        ar.setStudentName(rs.getString("nom_estudiante"));
        ar.setStudentDocNumber(rs.getString("numero_documento"));
        return ar;
    }
}
