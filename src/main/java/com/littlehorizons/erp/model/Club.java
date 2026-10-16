package com.littlehorizons.erp.model;

/**
 * Domain entity representing an extracurricular club (Club).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class Club {

    private int id;
    private String name;
    private String description;
    private Integer supervisorTeacherId;
    private int maxCapacity;
    private String meetingSchedule;
    private boolean active;

    // Relational join fields
    private String supervisorTeacherName;
    private int currentMemberCount;

    public Club() {
    }

    public Club(int id, String name, String description, Integer supervisorTeacherId,
                int maxCapacity, String meetingSchedule, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.supervisorTeacherId = supervisorTeacherId;
        this.maxCapacity = maxCapacity;
        this.meetingSchedule = meetingSchedule;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSupervisorTeacherId() { return supervisorTeacherId; }
    public void setSupervisorTeacherId(Integer supervisorTeacherId) { this.supervisorTeacherId = supervisorTeacherId; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public String getMeetingSchedule() { return meetingSchedule; }
    public void setMeetingSchedule(String meetingSchedule) { this.meetingSchedule = meetingSchedule; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getSupervisorTeacherName() { return supervisorTeacherName; }
    public void setSupervisorTeacherName(String supervisorTeacherName) { this.supervisorTeacherName = supervisorTeacherName; }

    public int getCurrentMemberCount() { return currentMemberCount; }
    public void setCurrentMemberCount(int currentMemberCount) { this.currentMemberCount = currentMemberCount; }

    public boolean hasAvailableSpot() {
        return currentMemberCount < maxCapacity;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%d/%d members) - Supervisor: %s",
                id, name, currentMemberCount, maxCapacity, supervisorTeacherName != null ? supervisorTeacherName : "None");
    }
}
