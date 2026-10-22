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
            ConsoleUtils.printHeader("MÓDULO 4: ACTIVIDADES EXTRACURRICULARES", "Little Horizons Educational ERP");
            System.out.println(" 1. Explorar Catálogo de Clubes Extracurriculares");
            System.out.println(" 2. Crear Nuevo Club");
            System.out.println(" 3. Inscribir Estudiante en Club (Valida Cupo Máximo)");
            System.out.println(" 4. Ver Miembros y Ocupación del Club");
            System.out.println(" 5. Retirar Estudiante de un Club");
            System.out.println(" 6. Ver Calendario de Eventos Escolares");
            System.out.println(" 7. Registrar Nuevo Evento Escolar");
            System.out.println(" 8. Inscribir Estudiante Participante en Evento");
            System.out.println(" 9. Registrar Resultados y Puestos de Competencia");
            System.out.println(" 0. Volver al Menú Principal");
            System.out.println();

            int choice = InputValidator.readInt("Seleccione una opción", 0, 9);

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
        ConsoleUtils.printSection("CATÁLOGO DE CLUBES EXTRACURRICULARES");
        List<Club> clubs = extracurricularController.getAllClubs();

        List<String> headers = List.of("ID", "Nombre del Club", "Docente Supervisor", "Horario", "Capacidad / Cupos", "Estado");
        List<List<String>> rows = new ArrayList<>();
        for (Club c : clubs) {
            String capacityDisplay = String.format("%d / %d %s",
                    c.getCurrentMemberCount(), c.getMaxCapacity(),
                    c.hasAvailableSpot() ? "[DISPONIBLE]" : "[LLENO]");
            rows.add(List.of(
                    String.valueOf(c.getId()),
                    c.getName(),
                    c.getSupervisorTeacherName() != null ? c.getSupervisorTeacherName() : "Ninguno",
                    c.getMeetingSchedule() != null ? c.getMeetingSchedule() : "",
                    capacityDisplay,
                    c.isActive() ? "Activo" : "Inactivo"
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void createClub() {
        ConsoleUtils.printSection("CREAR NUEVO CLUB");
        String name = InputValidator.readNonEmptyString("Nombre del Club");
        String desc = InputValidator.readNonEmptyString("Descripción del Club");
        int supervisorId = InputValidator.readInt("ID del Docente Supervisor", 1, 999999);
        int capacity = InputValidator.readInt("Cupo Máximo de Capacidad", 5, 100);
        String schedule = InputValidator.readNonEmptyString("Horario de Encuentro (ej. Martes 3-5 PM)");

        Club c = new Club(0, name, desc, supervisorId, capacity, schedule, true);
        Optional<Club> res = extracurricularController.createClub(c);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Club creado exitosamente con ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Error al crear club. Verifique que el nombre sea único.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void enrollInClub() {
        ConsoleUtils.printSection("INSCRIBIR ESTUDIANTE EN CLUB");
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        int clubId = InputValidator.readInt("Ingrese ID del Club", 1, 999999);

        Optional<ClubMembership> m = extracurricularController.joinClub(studentId, clubId);
        if (m.isPresent()) {
            ConsoleUtils.printSuccess("Estudiante inscrito en club exitosamente. ID de Membresía: " + m.get().getId());
        } else {
            ConsoleUtils.printError("No se pudo unir al club. El club puede estar LLENO o ya está inscrito.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewClubMembers() {
        ConsoleUtils.printSection("MIEMBROS DEL CLUB");
        int clubId = InputValidator.readInt("Ingrese ID del Club", 1, 999999);

        Optional<Club> clubOpt = extracurricularController.getClubById(clubId);
        if (clubOpt.isEmpty()) {
            ConsoleUtils.printError("Club no encontrado.");
            InputValidator.pressEnterToContinue();
            return;
        }

        Club c = clubOpt.get();
        System.out.println("Club: " + c.getName() + " | Supervisor: " + (c.getSupervisorTeacherName() != null ? c.getSupervisorTeacherName() : "Sin asignar"));
        System.out.printf("Capacidad: %d / %d (%s)%n", c.getCurrentMemberCount(), c.getMaxCapacity(),
                c.hasAvailableSpot() ? "Cupos Disponibles" : "Lleno");

        List<ClubMembership> members = extracurricularController.getClubMembers(clubId);
        List<String> headers = List.of("ID Membresía", "Nombre del Estudiante", "Fecha de Ingreso", "Estado");
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
        ConsoleUtils.printSection("RETIRAR ESTUDIANTE DE CLUB");
        int membershipId = InputValidator.readInt("Ingrese ID de Membresía", 1, 999999);
        String reason = InputValidator.readNonEmptyString("Motivo del Retiro");

        boolean ok = extracurricularController.withdrawFromClub(membershipId, reason);
        if (ok) {
            ConsoleUtils.printSuccess("Membresía actualizada a 'Retirada'. Cupo liberado exitosamente.");
        } else {
            ConsoleUtils.printError("Error al retirar del club.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void viewEventsCalendar() {
        ConsoleUtils.printSection("CALENDARIO DE EVENTOS ESCOLARES");
        List<SchoolEvent> events = extracurricularController.getAllEvents();

        List<String> headers = List.of("ID", "Nombre del Evento", "Tipo", "Fecha", "Lugar / Sede", "Competencia", "Participantes");
        List<List<String>> rows = new ArrayList<>();
        for (SchoolEvent ev : events) {
            rows.add(List.of(
                    String.valueOf(ev.getId()),
                    ev.getName(),
                    ev.getEventType(),
                    ev.getEventDate().toString(),
                    ev.getLocation() != null ? ev.getLocation() : "Campus",
                    ev.isHasCompetition() ? "SÍ" : "NO",
                    String.valueOf(ev.getParticipantCount())
            ));
        }
        ConsoleUtils.printTable(headers, rows);
        InputValidator.pressEnterToContinue();
    }

    private void createSchoolEvent() {
        ConsoleUtils.printSection("REGISTRAR EVENTO ESCOLAR");
        String name = InputValidator.readNonEmptyString("Nombre del Evento");
        String desc = InputValidator.readNonEmptyString("Descripción");
        String type = InputValidator.readNonEmptyString("Tipo de Evento (Académico, Cultural, Deportivo, Social, Científico)");
        LocalDate date = InputValidator.readDate("Fecha del Evento");
        String location = InputValidator.readNonEmptyString("Lugar / Sede");
        boolean comp = InputValidator.readBoolean("¿El evento incluye competencia con premiación/puestos?");
        int organizerId = InputValidator.readInt("ID del Docente Organizador", 1, 999999);

        SchoolEvent ev = new SchoolEvent(0, name, desc, type, date, location, comp, organizerId, true);
        Optional<SchoolEvent> res = extracurricularController.createEvent(ev);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Evento creado exitosamente con ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Error al crear evento.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void registerEventParticipant() {
        ConsoleUtils.printSection("INSCRIBIR PARTICIPANTE EN EVENTO");
        int eventId = InputValidator.readInt("Ingrese ID del Evento", 1, 999999);
        int studentId = InputValidator.readInt("Ingrese ID del Estudiante", 1, 999999);
        String role = InputValidator.readNonEmptyString("Rol (Competidor, Participante, Organizador, Asistente)");
        String position = InputValidator.readOptionalString("Puesto Inicial", "Participante");
        String obs = InputValidator.readOptionalString("Notas de Participación / Proyecto", "");

        Optional<EventParticipant> res = extracurricularController.registerParticipant(eventId, studentId, role, position, obs);
        if (res.isPresent()) {
            ConsoleUtils.printSuccess("Participante registrado con ID: " + res.get().getId());
        } else {
            ConsoleUtils.printError("Error al registrar participante.");
        }
        InputValidator.pressEnterToContinue();
    }

    private void recordEventResult() {
        ConsoleUtils.printSection("REGISTRAR RESULTADO / PUESTO DE COMPETENCIA");
        int eventId = InputValidator.readInt("Ingrese ID del Evento", 1, 999999);

        List<EventParticipant> list = extracurricularController.getEventParticipants(eventId);
        if (list.isEmpty()) {
            ConsoleUtils.printWarning("No hay participantes registrados para este evento.");
            InputValidator.pressEnterToContinue();
            return;
        }

        List<String> headers = List.of("ID Part.", "Nombre del Estudiante", "Rol", "Puesto", "Proyecto / Notas");
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

        int partId = InputValidator.readInt("Ingrese ID del Participante a actualizar", 1, 999999);
        String position = InputValidator.readNonEmptyString("Puesto Obtenido (1er Lugar, 2do Lugar, 3er Lugar, Participante)");
        String obs = InputValidator.readNonEmptyString("Observaciones del Logro");

        boolean ok = extracurricularController.recordCompetitionResult(partId, position, obs);
        if (ok) {
            ConsoleUtils.printSuccess("Puesto y logro registrados exitosamente.");
        } else {
            ConsoleUtils.printError("Error al registrar el puesto.");
        }
        InputValidator.pressEnterToContinue();
    }
}
