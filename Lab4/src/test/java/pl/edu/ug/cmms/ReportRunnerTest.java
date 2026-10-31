package pl.edu.ug.cmms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportRunnerTest {

    @Mock
    private List<WorkOrder> mockWorkOrders;

    @Test
    void shouldExecuteReportRunnerWithoutSpringUsingMockedOrdersAndFixedClock(@TempDir Path tempDir) throws IOException {
        LocalDateTime fixedInstant = LocalDateTime.parse("2026-09-21T06:00:00");
        Clock fixedClock = Clock.fixed(fixedInstant.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());

        List<WorkOrder> testOrders = List.of(
                // Zakończona awaria powyżej 4h (CRITICAL)
                new WorkOrder("WO-1", "M-01", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.CRITICAL,
                        WorkOrder.WorkOrderStatus.DONE, fixedInstant.minusHours(10), fixedInstant.minusHours(4)),
                // Otwarta awaria trwająca 20h > 16h (HIGH)
                new WorkOrder("WO-2", "M-01", WorkOrder.WorkOrderType.BREAKDOWN, WorkOrder.Priority.HIGH,
                        WorkOrder.WorkOrderStatus.OPEN, fixedInstant.minusHours(20), null)
        );

        when(mockWorkOrders.stream()).thenAnswer(inv -> testOrders.stream());

        Path tempReportFile = tempDir.resolve("test-raport.xml");

        Machine machine1 = new Machine("M-01", "Tokarka CNC 1", Machine.MachineType.CNC_LATHE, "A", java.time.LocalDate.parse("2020-01-01"));

        ReportRunner runner = new ReportRunner(
                new Tasks(),
                List.of(machine1),
                mockWorkOrders,
                3,
                tempReportFile.toString(),
                Duration.ofHours(4),
                Duration.ofHours(16),
                Duration.ofHours(36),
                Duration.ofHours(264),
                fixedClock
        );

        runner.run(null);

        String xmlContent = Files.readString(tempReportFile);

        assertAll(
                () -> assertTrue(Files.exists(tempReportFile), "Plik raportu XML powinien zostać wygenerowany"),
                () -> assertTrue(xmlContent.contains("CRITICAL"), "Raport powinien zawierać sekcję SLA")
        );

        verify(mockWorkOrders, atLeastOnce()).stream();
    }
}
