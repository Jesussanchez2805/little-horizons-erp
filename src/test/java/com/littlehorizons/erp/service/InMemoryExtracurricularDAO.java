package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ExtracurricularDAO;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory fake of {@link ExtracurricularDAO} for tests. Only the club and
 * membership operations are implemented; the rest are not needed by these tests.
 */
class InMemoryExtracurricularDAO implements ExtracurricularDAO {

    private final Map<Integer, Club> clubs = new LinkedHashMap<>();
    private final List<ClubMembership> memberships = new ArrayList<>();
    private int nextMembershipId = 1;

    @Override
    public Club createClub(Club club) {
        clubs.put(club.getId(), club);
        return club;
    }

    @Override
    public Optional<Club> findClubById(int id) {
        return Optional.ofNullable(clubs.get(id));
    }

    @Override
    public List<Club> findAllClubs() {
        return new ArrayList<>(clubs.values());
    }

    @Override
    public boolean updateClub(Club club) {
        return clubs.replace(club.getId(), club) != null;
    }

    @Override
    public ClubMembership registerMembership(ClubMembership membership) {
        membership.setId(nextMembershipId++);
        memberships.add(membership);
        return membership;
    }

    @Override
    public List<ClubMembership> findMembershipsByClub(int clubId) {
        return memberships.stream().filter(m -> m.getClubId() == clubId).toList();
    }

    @Override
    public List<ClubMembership> findMembershipsByStudent(int studentId) {
        return memberships.stream().filter(m -> m.getStudentId() == studentId).toList();
    }

    @Override
    public boolean withdrawMembership(int membershipId, String reason) {
        for (ClubMembership m : memberships) {
            if (m.getId() == membershipId) {
                m.setStatus("Retirada");
                m.setLeaveDate(LocalDate.now());
                m.setLeaveReason(reason);
                return true;
            }
        }
        return false;
    }

    @Override public SchoolEvent createEvent(SchoolEvent event) { throw new UnsupportedOperationException(); }
    @Override public Optional<SchoolEvent> findEventById(int id) { throw new UnsupportedOperationException(); }
    @Override public List<SchoolEvent> findAllEvents() { throw new UnsupportedOperationException(); }
    @Override public List<SchoolEvent> findEventsByType(String type) { throw new UnsupportedOperationException(); }
    @Override public EventParticipant registerParticipant(EventParticipant participant) { throw new UnsupportedOperationException(); }
    @Override public List<EventParticipant> findParticipantsByEvent(int eventId) { throw new UnsupportedOperationException(); }
    @Override public List<EventParticipant> findEventsByStudent(int studentId) { throw new UnsupportedOperationException(); }
    @Override public boolean updateParticipantPosition(int participantId, String position, String observations) { throw new UnsupportedOperationException(); }
}
