package com.littlehorizons.erp.exception;

/**
 * Checked exception raised when a student cannot be enrolled in a club.
 * Subclasses describe the specific business rule that was violated.
 */
public class ClubEnrollmentException extends Exception {

    public ClubEnrollmentException(String message) {
        super(message);
    }
}
