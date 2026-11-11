package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderTest {

    @Test
    void shouldCreateWorkOrderWhenValidData() {
        // poprawne zlecenie
        LocalDateTime reported = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime closed = LocalDateTime.of(2026, 3, 1, 12, 0);

        WorkOrder order = new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.BREAKDOWN,
                WorkOrder.Priority.HIGH, WorkOrder.WorkOrderStatus.DONE, reported, closed);

        assertAll(
                () -> assertEquals("WO-1", order.id()),
                () -> assertEquals("M-01", order.machineId()),
                () -> assertEquals(WorkOrder.WorkOrderType.BREAKDOWN, order.type()),
                () -> assertEquals(WorkOrder.Priority.HIGH, order.priority()),
                () -> assertEquals(WorkOrder.WorkOrderStatus.DONE, order.status()),
                () -> assertEquals(reported, order.reportedAt()),
                () -> assertEquals(closed, order.closedAt())
        );
    }

    @Test
    void shouldThrowExceptionWhenIdIsInvalid() {
        // puste id
        LocalDateTime reported = LocalDateTime.now();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new WorkOrder(null, "M-01",
                        WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.OPEN, reported, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new WorkOrder("", "M-01",
                        WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.OPEN, reported, null))
        );
    }

    @Test
    void shouldThrowExceptionWhenDoneWithoutClosedAt() {
        // DONE musi miec date zamkniecia
        LocalDateTime reported = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new WorkOrder("WO-1", "M-01",
                WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.DONE, reported, null));
    }

    @Test
    void shouldThrowExceptionWhenClosedAtIsBeforeReportedAt() {
        // zamkniete przed zgloszeniem
        LocalDateTime reported = LocalDateTime.of(2026, 3, 2, 12, 0);
        LocalDateTime closedBefore = LocalDateTime.of(2026, 3, 2, 11, 0);

        assertThrows(IllegalArgumentException.class, () -> new WorkOrder("WO-1", "M-01",
                WorkOrder.WorkOrderType.PREVENTIVE, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.OPEN, reported, closedBefore));
    }

    @Test
    void shouldThrowExceptionWhenMandatoryFieldsAreNull() {
        // brak wymaganych pol
        LocalDateTime now = LocalDateTime.now();
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new WorkOrder("WO-1", "M-01", null, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.OPEN, now, null)),
                () -> assertThrows(NullPointerException.class, () -> new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.INSPECTION, null, WorkOrder.WorkOrderStatus.OPEN, now, null)),
                () -> assertThrows(NullPointerException.class, () -> new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.INSPECTION, WorkOrder.Priority.LOW, null, now, null)),
                () -> assertThrows(NullPointerException.class, () -> new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.INSPECTION, WorkOrder.Priority.LOW, WorkOrder.WorkOrderStatus.OPEN, null, null))
        );
    }
}
