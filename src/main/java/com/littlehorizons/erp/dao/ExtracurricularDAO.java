package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Extracurricular Activities:
 * Clubs, Memberships, School Events, and Event Participants.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public interface ExtracurricularDAO {
    // Clubs
    Club createClub(Club club);
    Optional<Club> findClubById(int id);
    List<Club> findAllClubs();
    boolean updateClub(Club club);

    // Memberships
    ClubMembership registerMembership(ClubMembership membership);
    List<ClubMembership> findMembershipsByClub(int clubId);
    List<ClubMembership> findMembershipsByStudent(int studentId);
    boolean withdrawMembership(int membershipId, String reason);

    // Events
    SchoolEvent createEvent(SchoolEvent event);
    Optional<SchoolEvent> findEventById(int id);
    List<SchoolEvent> findAllEvents();
    List<SchoolEvent> findEventsByType(String type);

    // Participants
    EventParticipant registerParticipant(EventParticipant participant);
    List<EventParticipant> findParticipantsByEvent(int eventId);
    List<EventParticipant> findEventsByStudent(int studentId);
    boolean updateParticipantPosition(int participantId, String position, String observations);
}
