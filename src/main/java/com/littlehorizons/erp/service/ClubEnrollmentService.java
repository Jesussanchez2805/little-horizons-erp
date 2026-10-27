package com.littlehorizons.erp.service;

import com.littlehorizons.erp.dao.ExtracurricularDAO;
import com.littlehorizons.erp.exception.ClubEnrollmentException;
import com.littlehorizons.erp.exception.ClubFullException;
import com.littlehorizons.erp.exception.DuplicateMembershipException;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Business rules for enrolling students in extracurricular clubs.
 * The DAO is injected through the constructor (Dependency Inversion),
 * so the service can be tested without a database.
 */
public class ClubEnrollmentService {

    private static final String ACTIVE_STATUS = "Activa";

    private final ExtracurricularDAO extracurricularDAO;

    public ClubEnrollmentService(ExtracurricularDAO extracurricularDAO) {
        this.extracurricularDAO = Objects.requireNonNull(extracurricularDAO, "extracurricularDAO");
    }

    /**
     * Enrolls a student in a club after validating every business rule:
     * the club exists, is active, the student is not already an active member
     * and there is still room.
     *
     * @throws ClubEnrollmentException (or a subclass) when a rule is violated
     */
    public ClubMembership enroll(int studentId, int clubId, LocalDate joinDate) throws ClubEnrollmentException {
        Club club = findClub(clubId);
        if (!club.isActive()) {
            throw new ClubEnrollmentException("Club is not active: " + club.getName());
        }

        List<ClubMembership> activeMembers = findActiveMembers(clubId);

        if (activeMembers.stream().anyMatch(m -> m.getStudentId() == studentId)) {
            throw new DuplicateMembershipException(studentId, clubId);
        }
        if (activeMembers.size() >= club.getMaxCapacity()) {
            throw new ClubFullException(clubId, club.getMaxCapacity());
        }

        ClubMembership membership = new ClubMembership(0, studentId, clubId, joinDate, ACTIVE_STATUS, null, null);
        return extracurricularDAO.registerMembership(membership);
    }

    /**
     * @return number of free places in the club (never negative)
     */
    public int availableSpots(int clubId) throws ClubEnrollmentException {
        Club club = findClub(clubId);
        return Math.max(0, club.getMaxCapacity() - findActiveMembers(clubId).size());
    }

    private Club findClub(int clubId) throws ClubEnrollmentException {
        return extracurricularDAO.findClubById(clubId)
                .orElseThrow(() -> new ClubEnrollmentException("Club not found: " + clubId));
    }

    private List<ClubMembership> findActiveMembers(int clubId) {
        return extracurricularDAO.findMembershipsByClub(clubId).stream()
                .filter(m -> ACTIVE_STATUS.equals(m.getStatus()))
                .toList();
    }
}
