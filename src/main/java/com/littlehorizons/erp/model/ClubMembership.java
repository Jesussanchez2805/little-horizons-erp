package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing a student's membership in a club (Membresía Club).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ClubMembership {

    private int id;
    private int studentId;
    private int clubId;
    private LocalDate joinDate;
    private String status; // 'Activa', 'Retirada'
    private LocalDate leaveDate;
    private String leaveReason;

    // Relational join fields
    private String studentName;
    private String clubName;

    public ClubMembership() {
    }

    public ClubMembership(int id, int studentId, int clubId, LocalDate joinDate, String status, LocalDate leaveDate, String leaveReason) {
        this.id = id;
        this.studentId = studentId;
        this.clubId = clubId;
        this.joinDate = joinDate;
        this.status = status;
        this.leaveDate = leaveDate;
        this.leaveReason = leaveReason;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getLeaveDate() { return leaveDate; }
    public void setLeaveDate(LocalDate leaveDate) { this.leaveDate = leaveDate; }

    public String getLeaveReason() { return leaveReason; }
    public void setLeaveReason(String leaveReason) { this.leaveReason = leaveReason; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    @Override
    public String toString() {
        return String.format("[%d] Student: %s -> Club: %s (%s)",
                id, studentName != null ? studentName : studentId, clubName != null ? clubName : clubId, status);
    }
}
