package com.littlehorizons.erp.model;

import java.time.LocalDate;

/**
 * Domain entity representing an institutional event (Evento Escolar).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class SchoolEvent {

    private int id;
    private String name;
    private String description;
    private String eventType; // 'Académico', 'Cultural', 'Deportivo', 'Social', 'Científico'
    private LocalDate eventDate;
    private String location;
    private boolean hasCompetition;
    private Integer organizerTeacherId;
    private boolean active;

    // Relational join fields
    private String organizerTeacherName;
    private int participantCount;

    public SchoolEvent() {
    }

    public SchoolEvent(int id, String name, String description, String eventType, LocalDate eventDate,
                       String location, boolean hasCompetition, Integer organizerTeacherId, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.location = location;
        this.hasCompetition = hasCompetition;
        this.organizerTeacherId = organizerTeacherId;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isHasCompetition() { return hasCompetition; }
    public void setHasCompetition(boolean hasCompetition) { this.hasCompetition = hasCompetition; }

    public Integer getOrganizerTeacherId() { return organizerTeacherId; }
    public void setOrganizerTeacherId(Integer organizerTeacherId) { this.organizerTeacherId = organizerTeacherId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getOrganizerTeacherName() { return organizerTeacherName; }
    public void setOrganizerTeacherName(String organizerTeacherName) { this.organizerTeacherName = organizerTeacherName; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s at %s %s",
                id, name, eventType, eventDate, location, hasCompetition ? "[Competition]" : "");
    }
}
