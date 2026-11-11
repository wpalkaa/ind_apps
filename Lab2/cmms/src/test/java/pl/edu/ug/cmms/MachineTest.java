package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MachineTest {

    @Test
    void shouldCreateMachineWhenValidData() {
        // poprawne dane
        LocalDate date = LocalDate.of(2024, 1, 1);
        Machine machine = new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, "A", date);

        assertAll(
                () -> assertEquals("M-01", machine.id()),
                () -> assertEquals("Tokarka", machine.name()),
                () -> assertEquals(Machine.MachineType.CNC_LATHE, machine.type()),
                () -> assertEquals("A", machine.hall()),
                () -> assertEquals(date, machine.commissionedAt())
        );
    }

    @Test
    void shouldThrowExceptionWhenIdIsBlankOrNull() {
        // puste lub brakujące id
        LocalDate date = LocalDate.now();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Machine(null, "Tokarka", Machine.MachineType.CNC_LATHE, "A", date)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Machine("   ", "Tokarka", Machine.MachineType.CNC_LATHE, "A", date))
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlankOrNull() {
        // brak nazwy
        LocalDate date = LocalDate.now();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Machine("M-01", null, Machine.MachineType.CNC_LATHE, "A", date)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Machine("M-01", "", Machine.MachineType.CNC_LATHE, "A", date))
        );
    }

    @Test
    void shouldThrowExceptionWhenHallIsBlankOrNull() {
        // brak hali
        LocalDate date = LocalDate.now();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, null, date)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, "  ", date))
        );
    }

    @Test
    void shouldThrowExceptionWhenTypeOrDateIsNull() {
        // brak typu lub daty
        LocalDate date = LocalDate.now();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new Machine("M-01", "Tokarka", null, "A", date)),
                () -> assertThrows(NullPointerException.class, () -> new Machine("M-01", "Tokarka", Machine.MachineType.CNC_LATHE, "A", null))
        );
    }
}
