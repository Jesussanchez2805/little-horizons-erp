package com.littlehorizons.erp.exception;

/**
 * Raised when a student already has an active membership in the same club.
 */
public class DuplicateMembershipException extends ClubEnrollmentException {

    public DuplicateMembershipException(int studentId, int clubId) {
        super("Student " + studentId + " already has an active membership in club " + clubId);
    }
}
