package com.littlehorizons.erp.view;

import com.littlehorizons.erp.controller.ExtracurricularController;
import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ClubMembership;
import com.littlehorizons.erp.model.EventParticipant;
import com.littlehorizons.erp.model.SchoolEvent;
import com.littlehorizons.erp.util.ConsoleUtils;
import com.littlehorizons.erp.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Console view for the Extracurricular Activities module:
 * Clubs, Membership capacity constraints, School Events and Competition Rankings.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class ExtracurricularView {

    private final ExtracurricularController extracurricularController;

    public ExtracurricularView(ExtracurricularController extracurricularController) {
        this.extracurricularController = extracurricularController;
    }

    public void displayMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtils.printHeader("MODULE 4: EXTRACURRICULAR ACTIVITIES", "Little Horizons Educational ERP");
            System.out.println(" 1. Browse Extracurricular Clubs (Catálogo de Clubes)");
            System.out.println(" 2. Create New Club");
            System.out.println(" 3. Enroll Student in Club (Validates Max Capacity)");
            System.out.println(" 4. View Club Members & Occupancy");
            System.out.println(" 5. Withdraw Student from Club");
            System.out.println(" 6. View School Events Calendar (Eventos y Competencias)");
            System.out.println(" 7. Register New School Event");
            System.out.println(" 8. Register Student Participant in Event");
            System.out.println(" 9. Record Competition Results & Positions");
            System.out.println(" 0. Return to Main Menu");
            System.out.println();

            int choice = InputValidator.readInt("Select an option", 0, 9);

            switch (choice) {
                case 1 -> browseClubs();
                case 2 -> createClub();
                case 3 -> enrollInClub();
                case 4 -> viewClubMembers();
                case 5 -> withdrawFromClub();
                case 6 -> viewEventsCalendar();
                case 7 -> createSchoolEvent();
                case 8 -> registerEventParticipant();
                case 9 -> recordEventResult();
                case 0 -> back = true;
            }
        }
    }

    private void browseClubs() {
        ConsoleUtils.printSection("EXTRACURRICULAR CLUBS CATALOG");
        List<Club> clubs = extracurricularController.getAllClubs();

        List<String> headers = List.of("ID", "Club Name", "Supervisor", "Schedule", "Capacity", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (Club c : clubs) {
            String capacityDisplay = String.format("%d / %d %s",
                    c.getCurrentMemberCount(), c.getMaxCapacity(),
                    c.hasAvailableSpot() ? "[OPEN]" : "[FULL]");
            rows.add(List.of(
                    String.valueOf(c.getId()),
                    c.getName(),
                    c.getSupervisorTeacherName() != null ? c.getSupervisorTeacherName() : "None",
                    c.getMeetingSchedule() != null ? c.getMeetingSchedule() : "",
                    capacityDisplay,
                    c.isActive() ? "Active" : "Inactive"
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void createClub() {
        ConsoleUtils.printSection("CREATE NEW CLUB");
        String name = InputValidator.readNonEmptyString("Club Name");
        String desc = InputValidator.readNonEmptyString("Club Description");
        int supervisorId = InputValidator.readInt("Supervisor Teacher ID", 1, 999999);
        int capacity = InputValidator.readInt("Maximum Capacity Quota", 5, 100);
        String schedule = InputValidator.readNonEmptyString("Meeting Schedule (e.g. Martes 3-5 PM)");

        Club c = new Club(0, name, desc, supervisorId, capacity, schedule, true);
        Optional<Club> res = extracurricularController.createClub(c);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Club created with ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Failed to create club. Check if name is unique.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void enrollInClub() {
        ConsoleUtils.printSection("ENROLL STUDENT IN CLUB");
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        int clubId = InputValidator.readInt("Enter Club ID", 1, 999999);

        Optional<ClubMembership> m = extracurricularController.joinClub(studentId, clubId);
        if (m.isPresent()) {
            ConsoleUtils.printSuccess("Student enrolled in club successfully. Membership ID: " + m.get().getId());
        } else {
            ConsoleUtils.printError("Could not join club. Either the club is FULL or an error occurred.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewClubMembers() {
        ConsoleUtils.printSection("CLUB MEMBERS");
        int clubId = InputValidator.readInt("Enter Club ID", 1, 999999);

        Optional<Club> clubOpt = extracurricularController.getClubById(clubId);
        if (clubOpt.isEmpty()) {
            ConsoleUtils.printError("Club not found.");
            InputValidator.pressEnterToContinue();
            return;
        }

        Club c = clubOpt.get();
        System.out.println("Club: " + c.getName() + " | Supervisor: " + c.getSupervisorTeacherName());
        System.out.printf("Capacity: %d / %d (%s)%n", c.getCurrentMemberCount(), c.getMaxCapacity(),
                c.hasAvailableSpot() ? "Spots Available" : "Full");

        List<ClubMembership> members = extracurricularController.getClubMembers(clubId);
        List<String> headers = List.of("Membership ID", "Student Name", "Join Date", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (ClubMembership m : members) {
            rows.add(List.of(
                    String.valueOf(m.getId()),
                    m.getStudentName(),
                    m.getJoinDate().toString(),
                    m.getStatus()
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void withdrawFromClub() {
        ConsoleUtils.printSection("WITHDRAW FROM CLUB");
        int membershipId = InputValidator.readInt("Enter Membership ID", 1, 999999);
        String reason = InputValidator.readNonEmptyString("Withdrawal Reason");

        boolean ok = extracurricularController.withdrawFromClub(membershipId, reason);
        if (ok) {
            ConsoleUtils.printSuccess("Membership successfully updated to 'Retirada'. Spot freed.");
        } else {
            ConsoleUtils.printError("Failed to withdraw from club.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewEventsCalendar() {
        ConsoleUtils.printSection("SCHOOL EVENTS CALENDAR");
        List<SchoolEvent> events = extracurricularController.getAllEvents();

        List<String> headers = List.of("ID", "Event Name", "Type", "Date", "Location", "Competition", "Participants");
        List<List<String>> rows = new ArrayList<>();
        for (SchoolEvent ev : events) {
            rows.add(List.of(
                    String.valueOf(ev.getId()),
                    ev.getName(),
                    ev.getEventType(),
                    ev.getEventDate().toString(),
                    ev.getLocation() != null ? ev.getLocation() : "Campus",
                    ev.isHasCompetition() ? "YES" : "NO",
                    String.valueOf(ev.getParticipantCount())
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void createSchoolEvent() {
        ConsoleUtils.printSection("CREATE SCHOOL EVENT");
        String name = InputValidator.readNonEmptyString("Event Name");
        String desc = InputValidator.readNonEmptyString("Description");
        String type = InputValidator.readNonEmptyString("Event Type (Académico, Cultural, Deportivo, Social, Científico)");
        LocalDate date = InputValidator.readDate("Event Date");
        String location = InputValidator.readNonEmptyString("Location / Venue");
        boolean comp = InputValidator.readBoolean("Does the event feature a competition with rankings?");
        int organizerId = InputValidator.readInt("Organizer Teacher ID", 1, 999999);

        SchoolEvent ev = new SchoolEvent(0, name, desc, type, date, location, comp, organizerId, true);
        Optional<SchoolEvent> res = extracurricularController.createEvent(ev);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Event created with ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Failed to create event.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerEventParticipant() {
        ConsoleUtils.printSection("REGISTER EVENT PARTICIPANT");
        int eventId = InputValidator.readInt("Enter Event ID", 1, 999999);
        int studentId = InputValidator.readInt("Enter Student ID", 1, 999999);
        String role = InputValidator.readNonEmptyString("Role (Competidor, Participante, Organizador, Asistente)");
        String position = InputValidator.readOptionalString("Initial Position", "Participante");
        String obs = InputValidator.readOptionalString("Project / Participation Notes", "");

        Optional<EventParticipant> res = extracurricularController.registerParticipant(eventId, studentId, role, position, obs);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Participant registered with ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Failed to register participant.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordEventResult() {
        ConsoleUtils.printSection("RECORD COMPETITION RESULT / POSITION");
        int eventId = InputValidator.readInt("Enter Event ID", 1, 999999);

        List<EventParticipant> list = extracurricularController.getEventParticipants(eventId);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No participants registered for this event.");
            InputValidator.pressEnterToContinue();
            return;
        }

        List<String> headers = List.of("Part. ID", "Student Name", "Role", "Position", "Project / Notes");
        List<List<String>> rows = new ArrayList<>();
        for (EventParticipant ep : list) {
            rows.add(List.of(
                    String.valueOf(ep.getId()),
                    ep.getStudentName(),
                    ep.getRole(),
                    ep.getPosition() != null ? ep.getPosition() : "N/A",
                    ep.getObservations() != null ? ep.getObservations() : ""
            ));
        }
        ConsoleUtils.printTable(headers, rows);

        int partId = InputValidator.readInt("Enter Participant ID to update", 1, 999999);
        String position = InputValidator.readNonEmptyString("Position Obtained (1er Lugar, 2do Lugar, 3er Lugar, Participante)");
        String obs = InputValidator.readNonEmptyString("Achievement remarks");

        boolean ok = extracurricularController.recordCompetitionResult(partId, position, obs);
        if (ok) {
            ConsoleUtils.printSuccess("Position and achievement recorded successfully.");
        } else {
            ConsoleUtils.printError("Failed to record position.");
        }
        InputValidator.pressEnterToContinue();
    }
}
