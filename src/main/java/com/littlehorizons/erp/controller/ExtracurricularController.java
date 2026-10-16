package com.littlehorizons.erp.controller;

import com.littlehorizons.erp.dao.ExtracurricularDAO;
import com.littlehorizons.erp.dao.ExtracurricularDAOImpl;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.AppLogger;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller managing extracurricular clubs, memberships with capacity constraints,
 * and institutional school events.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ExtracurricularController {

    private final ExtracurricularDAO extracurricularDAO;

    public ExtracurricularController() {
        this(new ExtracurricularDAOImpl());
    }

    public ExtracurricularController(ExtracurricularDAO extracurricularDAO) {
        this.extracurricularDAO = extracurricularDAO;
    }

    // --- Clubs ---

    public Optional<Club> createClub(Club club) {
        return Optional.ofNullable(extracurricularDAO.createClub(club));
    }

    public Optional<Club> getClubById(int id) {
        return extracurricularDAO.findClubById(id);
    }

    public List<Club> getAllClubs() {
        return extracurricularDAO.findAllClubs();
    }

    public List<Club> getActiveClubs() {
        return extracurricularDAO.findAllClubs().stream()
                .filter(Club::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Enrolls a student into a club, strictly enforcing the maximum capacity quota.
     *
     * @param studentId Student ID
     * @param clubId Club ID
     * @return Optional containing created membership, or empty if quota is exceeded
     */
    public Optional<ClubMembership> joinClub(int studentId, int clubId) {
        Optional<Club> clubOpt = extracurricularDAO.findClubById(clubId);
        if (clubOpt.isEmpty()) {
            AppLogger.warning("Club ID " + clubId + " not found.");
            return Optional.empty();
        }

        Club club = clubOpt.get();
        if (!club.hasAvailableSpot()) {
            AppLogger.warning("Cannot join club '" + club.getName() + "': Maximum capacity reached (" +
                    club.getCurrentMemberCount() + "/" + club.getMaxCapacity() + ")");
            return Optional.empty();
        }

        ClubMembership m = new ClubMembership();
        m.setStudentId(studentId);
        m.setClubId(clubId);
        m.setJoinDate(LocalDate.now());
        m.setStatus("Activa");

        return Optional.ofNullable(extracurricularDAO.registerMembership(m));
    }

    public boolean withdrawFromClub(int membershipId, String reason) {
        return extracurricularDAO.withdrawMembership(membershipId, reason);
    }

    public List<ClubMembership> getClubMembers(int clubId) {
        return extracurricularDAO.findMembershipsByClub(clubId);
    }

    public List<ClubMembership> getStudentClubs(int studentId) {
        return extracurricularDAO.findMembershipsByStudent(studentId);
    }

    // --- Events ---

    public Optional<SchoolEvent> createEvent(SchoolEvent event) {
        return Optional.ofNullable(extracurricularDAO.createEvent(event));
    }

    public Optional<SchoolEvent> getEventById(int id) {
        return extracurricularDAO.findEventById(id);
    }

    public List<SchoolEvent> getAllEvents() {
        return extracurricularDAO.findAllEvents();
    }

    public Optional<EventParticipant> registerParticipant(int eventId, int studentId, String role, String initialPosition, String obs) {
        EventParticipant p = new EventParticipant();
        p.setEventId(eventId);
        p.setStudentId(studentId);
        p.setRole(role);
        p.setPosition(initialPosition != null ? initialPosition : "Participante");
        p.setObservations(obs);

        return Optional.ofNullable(extracurricularDAO.registerParticipant(p));
    }

    public List<EventParticipant> getEventParticipants(int eventId) {
        return extracurricularDAO.findParticipantsByEvent(eventId);
    }

    public boolean recordCompetitionResult(int participantId, String position, String observations) {
        return extracurricularDAO.updateParticipantPosition(participantId, position, observations);
    }
}
