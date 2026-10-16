package com.littlehorizons.erp.model;

/**
 * Domain entity representing a student's participation in an event (Participante Evento).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class EventParticipant {

    private int id;
    private int eventId;
    private int studentId;
    private String role; // 'Participante', 'Competidor', 'Organizador', 'Asistente'
    private String position; // e.g. '1er Lugar', '2do Lugar', '3er Lugar', 'Participante'
    private String observations;

    // Relational join fields
    private String studentName;
    private String eventName;

    public EventParticipant() {
    }

    public EventParticipant(int id, int eventId, int studentId, String role, String position, String observations) {
        this.id = id;
        this.eventId = eventId;
        this.studentId = studentId;
        this.role = role;
        this.position = position;
        this.observations = observations;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    @Override
    public String toString() {
        return String.format("[%d] Student: %s -> %s | Role: %s | Position: %s",
                id, studentName != null ? studentName : studentId, eventName != null ? eventName : eventId, role, position != null ? position : "N/A");
    }
}
