package com.littlehorizons.erp;

import com.littlehorizons.erp.model.Club;
import com.littlehorizons.erp.model.ResourceItem;
import com.littlehorizons.erp.model.Student;
import com.littlehorizons.erp.model.TeacherLoan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test validating business rules on domain models.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
class ModelValidationTest {

    @Test
    @DisplayName("Should assemble student full name correctly")
    void testStudentFullName() {
        Student s = new Student();
        s.setFirstName("Mateo");
        s.setSecondName("Alejandro");
        s.setFirstLastName("Ramírez");
        s.setSecondLastName("Gómez");

        assertEquals("Mateo Alejandro Ramírez Gómez", s.getFullName());
    }

    @Test
    @DisplayName("Should check club spots availability correctly")
    void testClubCapacityRule() {
        Club club = new Club(1, "Robotics", "Desc", 1, 20, "Tuesday", true);
        club.setCurrentMemberCount(19);
        assertTrue(club.hasAvailableSpot(), "Club with 19/20 members should have available spot");

        club.setCurrentMemberCount(20);
        assertFalse(club.hasAvailableSpot(), "Club with 20/20 members should be full");
    }

    @Test
    @DisplayName("Should evaluate resource availability based on status")
    void testResourceAvailability() {
        ResourceItem item = new ResourceItem(1, "LIB-001", "Book", 1, "Desc", LocalDate.now(), "Shelf 1", "Disponible");
        assertTrue(item.isAvailable());

        item.setStatus("Prestado");
        assertFalse(item.isAvailable());
    }

    @Test
    @DisplayName("Should evaluate teacher loan overdue logic correctly")
    void testTeacherLoanOverdue() {
        TeacherLoan loan = new TeacherLoan();
        loan.setStatus("Activo");
        loan.setExpectedReturnDate(LocalDate.now().minusDays(2));
        assertTrue(loan.isOverdue(), "Active loan with return date in the past should be overdue");

        loan.setExpectedReturnDate(LocalDate.now().plusDays(5));
        assertFalse(loan.isOverdue(), "Active loan with return date in future should not be overdue");

        loan.setStatus("Devuelto");
        loan.setExpectedReturnDate(LocalDate.now().minusDays(5));
        assertFalse(loan.isOverdue(), "Returned loan should never be flagged as overdue");
    }
}
