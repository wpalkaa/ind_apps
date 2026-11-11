package pl.edu.ug.cmms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TasksTest {

    private Tasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new Tasks();
    }

    @Test
    void shouldCountOnlyOpenAndInProgressOrdersGroupedByPriority() {
        // bierzemy tylko OPEN i IN_PROGRESS
        LocalDateTime now = LocalDateTime.now();
        List<WorkOrder> orders = List.of(
                new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL, WorkOrder.WorkOrderStatus.OPEN, now, null),
                new WorkOrder("WO-2", "M-02", WorkOrder.WorkOrderType.INSPECTION, WorkOrder.Priority.CRITICAL, WorkOrder.WorkOrderStatus.IN_PROGRESS, now, null),
                new WorkOrder("WO-3", "M-01", WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.OPEN, now, null),
                new WorkOrder("WO-4", "M-03", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL, WorkOrder.WorkOrderStatus.DONE, now, now),
                new WorkOrder("WO-5", "M-03", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.CANCELLED, now, null)
        );

        Map<WorkOrder.Priority, Long> result = tasks.countUnfinishedOrders(orders);

        assertAll(
                () -> assertEquals(2L, result.get(WorkOrder.Priority.CRITICAL)),
                () -> assertEquals(1L, result.get(WorkOrder.Priority.LOW)),
                () -> assertNull(result.get(WorkOrder.Priority.HIGH)),
                () -> assertEquals(2, result.size())
        );
    }

    @Test
    void shouldReturnTop3MachinesWithMostCompletedBreakdowns() {
        LocalDate date = LocalDate.of(2020, 1, 1);
        Machine m1 = new Machine("M-01", "M1", Machine.MachineType.CONVEYOR, "A", date);
        Machine m2 = new Machine("M-02", "M2", Machine.MachineType.CONVEYOR, "A", date);
        Machine m3 = new Machine("M-03", "M3", Machine.MachineType.CONVEYOR, "A", date);
        Machine m4 = new Machine("M-04", "M4", Machine.MachineType.CONVEYOR, "B", date);
        List<Machine> machines = List.of(m1, m2, m3, m4);

        LocalDateTime now = LocalDateTime.now();
        List<WorkOrder> orders = List.of(
                // 1 awaria DONE
                new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                // 3 awarie DONE
                new WorkOrder("WO-2", "M-02", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                new WorkOrder("WO-3", "M-02", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                new WorkOrder("WO-4", "M-02", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                // 2 awarie DONE
                new WorkOrder("WO-5", "M-03", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                new WorkOrder("WO-6", "M-03", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now),
                // inne statusy / typy (nie liczymy)
                new WorkOrder("WO-7", "M-04", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.OPEN, now, null),
                new WorkOrder("WO-8", "M-04", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.CANCELLED, now, null),
                new WorkOrder("WO-9", "M-04", WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, now, now)
        );

        List<Machine> top3 = tasks.top3BreakdownMachines(orders, machines);

        assertAll(
                () -> assertEquals(3, top3.size()),
                () -> assertEquals("M-02", top3.get(0).id()),
                () -> assertEquals("M-03", top3.get(1).id()),
                () -> assertEquals("M-01", top3.get(2).id())
        );
    }
}
