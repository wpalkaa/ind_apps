package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SampleDataTest {

    @Test
    void shouldReturnNonEmptySampleMachines() {
        // czy lista maszyn nie jest pusta
        List<Machine> machines = SampleData.machines();

        assertAll(
                () -> assertNotNull(machines),
                () -> assertEquals(9, machines.size()),
                () -> assertTrue(machines.stream().allMatch(m -> m.id() != null && !m.id().isBlank()))
        );
    }

    @Test
    void shouldReturnNonEmptySampleWorkOrders() {
        // czy zlecenia sie poprawnie ładują
        List<WorkOrder> orders = SampleData.workOrders();

        assertAll(
                () -> assertNotNull(orders),
                () -> assertEquals(45, orders.size()),
                () -> assertTrue(orders.stream().allMatch(o -> o.id() != null && !o.id().isBlank()))
        );
    }
}
