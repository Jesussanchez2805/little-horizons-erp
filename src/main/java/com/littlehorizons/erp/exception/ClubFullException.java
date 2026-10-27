package com.littlehorizons.erp.exception;

/**
 * Raised when a club has already reached its maximum capacity.
 */
public class ClubFullException extends ClubEnrollmentException {

    private final int clubId;
    private final int maxCapacity;

    public ClubFullException(int clubId, int maxCapacity) {
        super("Club " + clubId + " is full (maximum capacity: " + maxCapacity + ")");
        this.clubId = clubId;
        this.maxCapacity = maxCapacity;
    }

    public int getClubId() { return clubId; }

    public int getMaxCapacity() { return maxCapacity; }
}
