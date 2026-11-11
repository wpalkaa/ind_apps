package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void shouldCountUnfinishedOrders() {
        // tylko niezamknięte
        LocalDateTime now = LocalDateTime.now();
        List<WorkOrder> orders = List.of(
                new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL, WorkOrder.WorkOrderStatus.OPEN, now, null),
                new WorkOrder("WO-2", "M-02", WorkOrder.WorkOrderType.INSPECTION, WorkOrder.Priority.CRITICAL, WorkOrder.WorkOrderStatus.IN_PROGRESS, now, null),
                new WorkOrder("WO-3", "M-03", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now)
        );

        Map<WorkOrder.Priority, Long> result = Main.countUnfinishedOrders(orders);

        assertAll(
                () -> assertEquals(2L, result.get(WorkOrder.Priority.CRITICAL)),
                () -> assertNull(result.get(WorkOrder.Priority.HIGH))
        );
    }

    @Test
    void shouldFindTop3BreakdownMachinesExcludingCancelled() {
        LocalDate date = LocalDate.of(2022, 1, 1);
        Machine m1 = new Machine("M-01", "M1", Machine.MachineType.CONVEYOR, "A", date);
        Machine m2 = new Machine("M-02", "M2", Machine.MachineType.CONVEYOR, "A", date);
        Machine m3 = new Machine("M-03", "M3", Machine.MachineType.CONVEYOR, "A", date);
        Machine m4 = new Machine("M-04", "M4", Machine.MachineType.CONVEYOR, "B", date);
        List<Machine> machines = List.of(m1, m2, m3, m4);

        LocalDateTime now = LocalDateTime.now();
        List<WorkOrder> orders = List.of(
                new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.OPEN, now, null),
                new WorkOrder("WO-2", "M-02", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.IN_PROGRESS, now, null),
                new WorkOrder("WO-3", "M-02", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                new WorkOrder("WO-4", "M-03", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                // anulowane pomijamy
                new WorkOrder("WO-5", "M-04", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.CANCELLED, now, null)
        );

        List<Machine> top = Main.top3BreakdowMachines(orders, machines);

        assertAll(
                () -> assertEquals(3, top.size()),
                () -> assertEquals("M-02", top.get(0).id()),
                // remis: alfabetycznie po id
                () -> assertEquals("M-01", top.get(1).id()),
                () -> assertEquals("M-03", top.get(2).id())
        );
    }

    @Test
    void shouldExecuteMainWithoutExceptions() {
        // dymny test maina
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}
