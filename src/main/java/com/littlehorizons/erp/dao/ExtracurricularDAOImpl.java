package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.AppLogger;
import com.littlehorizons.erp.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ExtracurricularDAO.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ExtracurricularDAOImpl implements ExtracurricularDAO {

    @Override
    public Club createClub(Club club) {
        String sql = "INSERT INTO clubes (nombre_club, descripcion, id_profesor_supervisor, " +
                     "capacidad_maxima, horario_reunion, esta_activo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            if (club.getSupervisorTeacherId() != null) {
                stmt.setInt(3, club.getSupervisorTeacherId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, club.getMaxCapacity());
            stmt.setString(5, club.getMeetingSchedule());
            stmt.setBoolean(6, club.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        club.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Club created: " + club.getName());
                return club;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error inserting club: " + club.getName(), e);
        }
        return null;
    }

    @Override
    public Optional<Club> findClubById(int id) {
        String sql = "SELECT c.*, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "(SELECT COUNT(*) FROM membresias_club mc WHERE mc.id_club = c.id_club AND mc.estado_membresia = 'Activa') AS total_miembros " +
                     "FROM clubes c " +
                     "LEFT JOIN profesores p ON c.id_profesor_supervisor = p.id_profesor " +
                     "WHERE c.id_club = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClub(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding club ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Club> findAllClubs() {
        List<Club> list = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "(SELECT COUNT(*) FROM membresias_club mc WHERE mc.id_club = c.id_club AND mc.estado_membresia = 'Activa') AS total_miembros " +
                     "FROM clubes c " +
                     "LEFT JOIN profesores p ON c.id_profesor_supervisor = p.id_profesor " +
                     "ORDER BY c.nombre_club";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToClub(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all clubs", e);
        }
        return list;
    }

    @Override
    public boolean updateClub(Club club) {
        String sql = "UPDATE clubes SET nombre_club = ?, descripcion = ?, id_profesor_supervisor = ?, " +
                     "capacidad_maxima = ?, horario_reunion = ?, esta_activo = ? WHERE id_club = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            if (club.getSupervisorTeacherId() != null) {
                stmt.setInt(3, club.getSupervisorTeacherId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, club.getMaxCapacity());
            stmt.setString(5, club.getMeetingSchedule());
            stmt.setBoolean(6, club.isActive());
            stmt.setInt(7, club.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error updating club ID: " + club.getId(), e);
            return false;
        }
    }

    @Override
    public ClubMembership registerMembership(ClubMembership m) {
        String sql = "INSERT INTO membresias_club (id_estudiante, id_club, fecha_ingreso, estado_membresia) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, m.getStudentId());
            stmt.setInt(2, m.getClubId());
            stmt.setDate(3, Date.valueOf(m.getJoinDate()));
            stmt.setString(4, m.getStatus() != null ? m.getStatus() : "Activa");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Student " + m.getStudentId() + " registered in club " + m.getClubId());
                return m;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error registering club membership for student: " + m.getStudentId(), e);
        }
        return null;
    }

    @Override
    public List<ClubMembership> findMembershipsByClub(int clubId) {
        List<ClubMembership> list = new ArrayList<>();
        String sql = "SELECT mc.*, c.nombre_club, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM membresias_club mc " +
                     "INNER JOIN clubes c ON mc.id_club = c.id_club " +
                     "INNER JOIN estudiantes e ON mc.id_estudiante = e.id_estudiante " +
                     "WHERE mc.id_club = ? AND mc.estado_membresia = 'Activa' ORDER BY nom_estudiante";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMembership(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding memberships for club: " + clubId, e);
        }
        return list;
    }

    @Override
    public List<ClubMembership> findMembershipsByStudent(int studentId) {
        List<ClubMembership> list = new ArrayList<>();
        String sql = "SELECT mc.*, c.nombre_club, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM membresias_club mc " +
                     "INNER JOIN clubes c ON mc.id_club = c.id_club " +
                     "INNER JOIN estudiantes e ON mc.id_estudiante = e.id_estudiante " +
                     "WHERE mc.id_estudiante = ? ORDER BY mc.fecha_ingreso DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMembership(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding memberships for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public boolean withdrawMembership(int membershipId, String reason) {
        String sql = "UPDATE membresias_club SET estado_membresia = 'Retirada', fecha_retiro = ?, " +
                     "motivo_retiro = ? WHERE id_membresia = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setString(2, reason);
            stmt.setInt(3, membershipId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error withdrawing club membership: " + membershipId, e);
            return false;
        }
    }

    @Override
    public SchoolEvent createEvent(SchoolEvent ev) {
        String sql = "INSERT INTO eventos (nombre_evento, descripcion, tipo_evento, fecha_evento, " +
                     "lugar, tiene_competencia, id_organizador, esta_activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ev.getName());
            stmt.setString(2, ev.getDescription());
            stmt.setString(3, ev.getEventType());
            stmt.setDate(4, Date.valueOf(ev.getEventDate()));
            stmt.setString(5, ev.getLocation());
            stmt.setBoolean(6, ev.isHasCompetition());
            if (ev.getOrganizerTeacherId() != null) {
                stmt.setInt(7, ev.getOrganizerTeacherId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            stmt.setBoolean(8, ev.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ev.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Event created: " + ev.getName());
                return ev;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error creating event: " + ev.getName(), e);
        }
        return null;
    }

    @Override
    public Optional<SchoolEvent> findEventById(int id) {
        String sql = "SELECT ev.*, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "(SELECT COUNT(*) FROM participantes_evento pe WHERE pe.id_evento = ev.id_evento) AS total_part " +
                     "FROM eventos ev " +
                     "LEFT JOIN profesores p ON ev.id_organizador = p.id_profesor " +
                     "WHERE ev.id_evento = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding event ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<SchoolEvent> findAllEvents() {
        List<SchoolEvent> list = new ArrayList<>();
        String sql = "SELECT ev.*, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "(SELECT COUNT(*) FROM participantes_evento pe WHERE pe.id_evento = ev.id_evento) AS total_part " +
                     "FROM eventos ev " +
                     "LEFT JOIN profesores p ON ev.id_organizador = p.id_profesor " +
                     "ORDER BY ev.fecha_evento ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToEvent(rs));
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching all events", e);
        }
        return list;
    }

    @Override
    public List<SchoolEvent> findEventsByType(String type) {
        List<SchoolEvent> list = new ArrayList<>();
        String sql = "SELECT ev.*, CONCAT(p.primer_nombre, ' ', p.primer_apellido) AS nom_profesor, " +
                     "(SELECT COUNT(*) FROM participantes_evento pe WHERE pe.id_evento = ev.id_evento) AS total_part " +
                     "FROM eventos ev " +
                     "LEFT JOIN profesores p ON ev.id_organizador = p.id_profesor " +
                     "WHERE ev.tipo_evento = ? ORDER BY ev.fecha_evento ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEvent(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding events by type: " + type, e);
        }
        return list;
    }

    @Override
    public EventParticipant registerParticipant(EventParticipant p) {
        String sql = "INSERT INTO participantes_evento (id_evento, id_estudiante, rol_participacion, " +
                     "posicion_obtenida, observaciones) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getEventId());
            stmt.setInt(2, p.getStudentId());
            stmt.setString(3, p.getRole() != null ? p.getRole() : "Participante");
            stmt.setString(4, p.getPosition());
            stmt.setString(5, p.getObservations());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                AppLogger.info("Student " + p.getStudentId() + " registered for event " + p.getEventId());
                return p;
            }
        } catch (SQLException e) {
            AppLogger.severe("Error registering participant for event: " + p.getEventId(), e);
        }
        return null;
    }

    @Override
    public List<EventParticipant> findParticipantsByEvent(int eventId) {
        List<EventParticipant> list = new ArrayList<>();
        String sql = "SELECT pe.*, ev.nombre_evento, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM participantes_evento pe " +
                     "INNER JOIN eventos ev ON pe.id_evento = ev.id_evento " +
                     "INNER JOIN estudiantes e ON pe.id_estudiante = e.id_estudiante " +
                     "WHERE pe.id_evento = ? ORDER BY pe.posicion_obtenida, nom_estudiante";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToParticipant(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error fetching participants for event: " + eventId, e);
        }
        return list;
    }

    @Override
    public List<EventParticipant> findEventsByStudent(int studentId) {
        List<EventParticipant> list = new ArrayList<>();
        String sql = "SELECT pe.*, ev.nombre_evento, CONCAT(e.primer_nombre, ' ', e.primer_apellido) AS nom_estudiante " +
                     "FROM participantes_evento pe " +
                     "INNER JOIN eventos ev ON pe.id_evento = ev.id_evento " +
                     "INNER JOIN estudiantes e ON pe.id_estudiante = e.id_estudiante " +
                     "WHERE pe.id_estudiante = ? ORDER BY ev.fecha_evento DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToParticipant(rs));
                }
            }
        } catch (SQLException e) {
            AppLogger.severe("Error finding events for student: " + studentId, e);
        }
        return list;
    }

    @Override
    public boolean updateParticipantPosition(int participantId, String position, String observations) {
        String sql = "UPDATE participantes_evento SET posicion_obtenida = ?, observaciones = ? WHERE id_participacion = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, position);
            stmt.setString(2, observations);
            stmt.setInt(3, participantId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            AppLogger.severe("Error updating participant position: " + participantId, e);
            return false;
        }
    }

    private Club mapResultSetToClub(ResultSet rs) throws SQLException {
        Club c = new Club();
        c.setId(rs.getInt("id_club"));
        c.setName(rs.getString("nombre_club"));
        c.setDescription(rs.getString("descripcion"));
        int sup = rs.getInt("id_profesor_supervisor");
        if (!rs.wasNull()) c.setSupervisorTeacherId(sup);
        c.setMaxCapacity(rs.getInt("capacidad_maxima"));
        c.setMeetingSchedule(rs.getString("horario_reunion"));
        c.setActive(rs.getBoolean("esta_activo"));
        c.setSupervisorTeacherName(rs.getString("nom_profesor"));
        c.setCurrentMemberCount(rs.getInt("total_miembros"));
        return c;
    }

    private ClubMembership mapResultSetToMembership(ResultSet rs) throws SQLException {
        ClubMembership m = new ClubMembership();
        m.setId(rs.getInt("id_membresia"));
        m.setStudentId(rs.getInt("id_estudiante"));
        m.setClubId(rs.getInt("id_club"));
        m.setJoinDate(rs.getDate("fecha_ingreso").toLocalDate());
        m.setStatus(rs.getString("estado_membresia"));
        Date l = rs.getDate("fecha_retiro");
        if (l != null) m.setLeaveDate(l.toLocalDate());
        m.setLeaveReason(rs.getString("motivo_retiro"));
        m.setClubName(rs.getString("nombre_club"));
        m.setStudentName(rs.getString("nom_estudiante"));
        return m;
    }

    private SchoolEvent mapResultSetToEvent(ResultSet rs) throws SQLException {
        SchoolEvent ev = new SchoolEvent();
        ev.setId(rs.getInt("id_evento"));
        ev.setName(rs.getString("nombre_evento"));
        ev.setDescription(rs.getString("descripcion"));
        ev.setEventType(rs.getString("tipo_evento"));
        ev.setEventDate(rs.getDate("fecha_evento").toLocalDate());
        ev.setLocation(rs.getString("lugar"));
        ev.setHasCompetition(rs.getBoolean("tiene_competencia"));
        int org = rs.getInt("id_organizador");
        if (!rs.wasNull()) ev.setOrganizerTeacherId(org);
        ev.setActive(rs.getBoolean("esta_activo"));
        ev.setOrganizerTeacherName(rs.getString("nom_profesor"));
        ev.setParticipantCount(rs.getInt("total_part"));
        return ev;
    }

    private EventParticipant mapResultSetToParticipant(ResultSet rs) throws SQLException {
        EventParticipant ep = new EventParticipant();
        ep.setId(rs.getInt("id_participacion"));
        ep.setEventId(rs.getInt("id_evento"));
        ep.setStudentId(rs.getInt("id_estudiante"));
        ep.setRole(rs.getString("rol_participacion"));
        ep.setPosition(rs.getString("posicion_obtenida"));
        ep.setObservations(rs.getString("observaciones"));
        ep.setEventName(rs.getString("nombre_evento"));
        ep.setStudentName(rs.getString("nom_estudiante"));
        return ep;
    }
}
