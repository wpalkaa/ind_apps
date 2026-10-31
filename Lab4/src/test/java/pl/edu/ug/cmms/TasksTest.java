package pl.edu.ug.cmms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksTest {

    private Tasks tasks;

    @Mock
    private List<WorkOrder> mockWorkOrders;

    private final Map<Priority, Duration> slaLimits = Map.of(
            Priority.CRITICAL, Duration.ofHours(4),
            Priority.HIGH, Duration.ofHours(16),
            Priority.NORMAL, Duration.ofHours(36),
            Priority.LOW, Duration.ofHours(264)
    );

    @BeforeEach
    void setUp() {
        tasks = new Tasks();
    }

    @Test
    void shouldCountSlaExceededBreakdownsUsingMockedDataSource() {
        LocalDateTime base = LocalDateTime.parse("2026-03-01T10:00:00");

        List<WorkOrder> testOrders = List.of(
                // CRITICAL (limit 4h) - trwała 5h -> PRZEKROCZONY
                new WorkOrder("WO-1", "M-01", WorkOrderType.BREAKDOWN, Priority.CRITICAL, WorkOrderStatus.DONE, base, base.plusHours(5)),
                // CRITICAL (limit 4h) - trwała 3h -> W NORMIE
                new WorkOrder("WO-2", "M-01", WorkOrderType.BREAKDOWN, Priority.CRITICAL, WorkOrderStatus.DONE, base, base.plusHours(3)),
                // HIGH (limit 16h) - trwała 18h -> PRZEKROCZONY
                new WorkOrder("WO-3", "M-02", WorkOrderType.BREAKDOWN, Priority.HIGH, WorkOrderStatus.DONE, base, base.plusHours(18)),
                // NORMAL (limit 36h) - trwała 40h -> PRZEKROCZONY
                new WorkOrder("WO-4", "M-03", WorkOrderType.BREAKDOWN, Priority.NORMAL, WorkOrderStatus.DONE, base, base.plusHours(40)),
                // LOW (limit 264h) - trwała 100h -> W NORMIE
                new WorkOrder("WO-5", "M-04", WorkOrderType.BREAKDOWN, Priority.LOW, WorkOrderStatus.DONE, base, base.plusHours(100)),
                // CRITICAL ale CANCELLED (nie DONE) -> IGNOROWANY
                new WorkOrder("WO-6", "M-01", WorkOrderType.BREAKDOWN, Priority.CRITICAL, WorkOrderStatus.CANCELLED, base, base.plusHours(10)),
                // CRITICAL ale PREVENTIVE (nie BREAKDOWN) -> IGNOROWANY
                new WorkOrder("WO-7", "M-01", WorkOrderType.PREVENTIVE, Priority.CRITICAL, WorkOrderStatus.DONE, base, base.plusHours(10))
        );

        when(mockWorkOrders.stream()).thenAnswer(inv -> testOrders.stream());

        Map<Priority, Long> result = tasks.countSlaExceededBreakdowns(mockWorkOrders, slaLimits);

        assertAll(
                () -> assertEquals(1L, result.get(Priority.CRITICAL)),
                () -> assertEquals(1L, result.get(Priority.HIGH)),
                () -> assertEquals(1L, result.get(Priority.NORMAL)),
                () -> assertNull(result.get(Priority.LOW))
        );

        verify(mockWorkOrders).stream();
    }

    @Test
    @DisplayName("Punkt 3: Powinien wyszukiwać niezakończone zlecenia otwarte dłużej niż limit SLA z zegarem o stałej chwili")
    void shouldFindUnfinishedOrdersExceedingSlaUsingFixedClockAndMockedDataSource() {
        // Zegar ustawiony na stałą chwilę demonstracyjną (2026-09-21 06:00:00)
        LocalDateTime fixedInstant = LocalDateTime.parse("2026-09-21T06:00:00");
        Clock fixedClock = Clock.fixed(fixedInstant.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now(fixedClock);

        List<WorkOrder> testOrders = List.of(
                // WO-10: HIGH (limit 16h), otwarte 2026-09-20 06:00 -> upłynęły 24h -> PRZEKROCZONE
                new WorkOrder("WO-10", "M-01", WorkOrderType.BREAKDOWN, Priority.HIGH, WorkOrderStatus.OPEN, fixedInstant.minusHours(24), null),
                // WO-11: HIGH (limit 16h), otwarte 2026-09-21 00:00 -> upłynęło 6h -> W NORMIE
                new WorkOrder("WO-11", "M-01", WorkOrderType.BREAKDOWN, Priority.HIGH, WorkOrderStatus.IN_PROGRESS, fixedInstant.minusHours(6), null),
                // WO-12: CRITICAL (limit 4h), otwarte 2026-09-21 01:00 -> upłynęło 5h -> PRZEKROCZONE
                new WorkOrder("WO-12", "M-02", WorkOrderType.PREVENTIVE, Priority.CRITICAL, WorkOrderStatus.OPEN, fixedInstant.minusHours(5), null),
                // WO-13: HIGH (limit 16h), otwarte 30h temu ale już DONE -> IGNOROWANE (nie jest otwarte)
                new WorkOrder("WO-13", "M-03", WorkOrderType.BREAKDOWN, Priority.HIGH, WorkOrderStatus.DONE, fixedInstant.minusHours(30), fixedInstant.minusHours(20)),
                // WO-14: HIGH (limit 16h), otwarte 30h temu ale CANCELLED -> IGNOROWANE
                new WorkOrder("WO-14", "M-03", WorkOrderType.BREAKDOWN, Priority.HIGH, WorkOrderStatus.CANCELLED, fixedInstant.minusHours(30), null)
        );

        when(mockWorkOrders.stream()).thenAnswer(inv -> testOrders.stream());

        List<WorkOrder> overdueOrders = tasks.unfinishedOrdersExceedingSla(mockWorkOrders, slaLimits, now);

        List<String> overdueIds = overdueOrders.stream().map(WorkOrder::id).toList();

        assertAll(
                () -> assertEquals(2, overdueOrders.size()),
                () -> assertTrue(overdueIds.contains("WO-10")),
                () -> assertTrue(overdueIds.contains("WO-12")),
                () -> assertFalse(overdueIds.contains("WO-11")),
                () -> assertFalse(overdueIds.contains("WO-13")),
                () -> assertFalse(overdueIds.contains("WO-14"))
        );

        verify(mockWorkOrders).stream();
    }

    @Test
    @DisplayName("Punkt 2 i 3: Zastąpienie źródła danych SampleData statyczną atrapą Mockito (mockStatic)")
    void shouldWorkWithStaticMockedSampleData() {
        LocalDateTime fixedInstant = LocalDateTime.parse("2026-09-21T06:00:00");
        Clock fixedClock = Clock.fixed(fixedInstant.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now(fixedClock);

        List<WorkOrder> sampleOrders = List.of(
                new WorkOrder("WO-S1", "M-01", WorkOrderType.BREAKDOWN, Priority.CRITICAL, WorkOrderStatus.DONE,
                        fixedInstant.minusHours(10), fixedInstant.minusHours(5)), // trwała 5h > 4h -> przekroczona
                new WorkOrder("WO-S2", "M-02", WorkOrderType.BREAKDOWN, Priority.HIGH, WorkOrderStatus.OPEN,
                        fixedInstant.minusHours(20), null) // trwa 20h > 16h -> przekroczona
        );

        try (MockedStatic<SampleData> sampleDataMock = Mockito.mockStatic(SampleData.class)) {
            sampleDataMock.when(SampleData::workOrders).thenReturn(sampleOrders);

            List<WorkOrder> ordersFromMockedSource = SampleData.workOrders();

            Map<Priority, Long> slaExceeded = tasks.countSlaExceededBreakdowns(ordersFromMockedSource, slaLimits);
            List<WorkOrder> overdueUnfinished = tasks.unfinishedOrdersExceedingSla(ordersFromMockedSource, slaLimits, now);

            assertAll(
                    () -> assertEquals(1L, slaExceeded.get(Priority.CRITICAL)),
                    () -> assertEquals(1, overdueUnfinished.size()),
                    () -> assertEquals("WO-S2", overdueUnfinished.get(0).id())
            );

            sampleDataMock.verify(SampleData::workOrders, times(1));
        }
    }
}
