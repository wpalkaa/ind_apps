package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component 
public class ReportRunner implements ApplicationRunner {
    
    private final Tasks tasks;
    private final List<Machine> machines;
    private final List<WorkOrder> workOrders;
    private final int topBreakdownMachinesLimit;
    private final String filePath;
    private final Map<WorkOrder.Priority, Duration> slaLimits;
    private final Clock clock;
    private final ReadMachinesData readMachinesData;
    private final VendorClient vendorClient;

    public ReportRunner(
        Tasks tasks,
        List<Machine> machines,
        List<WorkOrder> workOrders,
        @Value("${cmms.report.ranking-limit:3}") int topBreakdownMachinesLimit,
        @Value("${cmms.report.file-path:raport.xml}") String filePath,
        @Value("${cmms.sla.critical:4h}") Duration slaCritical,
        @Value("${cmms.sla.high:16h}") Duration slaHigh,
        @Value("${cmms.sla.normal:36h}") Duration slaNormal,
        @Value("${cmms.sla.low:264h}") Duration slaLow,
        Clock clock,
        ReadMachinesData readMachinesData,
        VendorClient vendorClient
    ) {
        this.tasks = tasks;
        this.machines = machines;
        this.workOrders = workOrders;
        this.topBreakdownMachinesLimit = topBreakdownMachinesLimit;
        this.filePath = filePath;
        this.slaLimits = Map.of(
            WorkOrder.Priority.CRITICAL, slaCritical,
            WorkOrder.Priority.HIGH, slaHigh,
            WorkOrder.Priority.NORMAL, slaNormal,
            WorkOrder.Priority.LOW, slaLow
        );
        this.clock = clock;
        this.readMachinesData = readMachinesData;
        this.vendorClient = vendorClient;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        // import maszyn z CSV przy starcie i dołączenie poprawnych maszyn do maszyn zakładu
        ImportResult importResult = readMachinesData.readMachines(machines);
        machines.addAll(importResult.acceptedMachines());

        System.out.println("## Podsumowanie importu maszyn z pliku CSV:");
        System.out.println("  Liczba przyjętych: " + importResult.acceptedCount());
        System.out.println("  Liczba odrzuconych: " + importResult.rejectedCount());

        if (!importResult.acceptedMachines().isEmpty()) {
            System.out.println("\n## Przyjęte wiersze (dołączone do zakładu):");
            for (Machine m : importResult.acceptedMachines()) {
                System.out.println("  - " + m);
            }
        }

        if (!importResult.rejectedRows().isEmpty()) {
            System.out.println("\n## Odrzucone wiersze:");
            for (RejectedRow r : importResult.rejectedRows()) {
                System.out.println("  - Linia " + r.lineNumber() + ": " + r.reason() + " [" + r.rawLine() + "]");
            }
        }

        System.out.println("\n## Niezakończone zlecenia wg priorytetu:");
        Map<WorkOrder.Priority, Long> countUnfinishedOrders = tasks.countUnfinishedOrders(workOrders);
        countUnfinishedOrders.forEach((p, c) -> System.out.println("  " + p + ": " + c));

        System.out.println("\n## 3 maszyny z największą liczbą awarii:");
        List<Machine> topMachines = tasks.topBreakdownMachines(workOrders, machines, topBreakdownMachinesLimit);
        for (int i = 0; i < topMachines.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + topMachines.get(i));
        }

        System.out.println("\n## Zakończone awarie przekraczające limit SLA wg priorytetu:");
        Map<WorkOrder.Priority, Long> slaExceeded = tasks.countSlaExceededBreakdowns(workOrders, slaLimits);
        for (WorkOrder.Priority priority : WorkOrder.Priority.values()) {
            System.out.println("  " + priority + ": " + slaExceeded.getOrDefault(priority, 0L));
        }

        LocalDateTime now = LocalDateTime.now(clock);
        List<WorkOrder> overdueUnfinished = tasks.unfinishedOrdersExceedingSla(workOrders, slaLimits, now);
        
        System.out.println("\n## Niezakończone zlecenia otwarte dłużej niż limit SLA (czas: " + now + "):");
        for (WorkOrder order : overdueUnfinished) {
            Duration elapsed = Duration.between(order.reportedAt(), now);
            Duration limit = slaLimits.get(order.priority());
            System.out.println("  " + order.id() + " [" + order.priority() + "]: otwarte od " 
                + order.reportedAt() + " (trwa " + elapsed.toHours() + "h, limit: " + limit.toHours() + "h)");
        }

        List<ReportXML.PriorityCountXml> unfinishedXml = countUnfinishedOrders.entrySet().stream()
            .map(e -> new ReportXML.PriorityCountXml(e.getKey(), e.getValue()))
            .toList();

        List<ReportXML.MachineBreakdownXml> breakdownXml = topMachines.stream()
            .map(machine -> {
                long breakdowns = workOrders.stream()
                        .filter(wo -> wo.machineId().equals(machine.id())
                                && wo.type() == WorkOrder.WorkOrderType.BREAKDOWN
                                && wo.status() == WorkOrder.WorkOrderStatus.DONE)
                        .count();
                return new ReportXML.MachineBreakdownXml(machine.id(), breakdowns);
            })
            .toList();
        
        List<ReportXML.SlaExceededBreakdown> slaExceededBreakdownsXml = slaExceeded.entrySet().stream()
            .map(e -> new ReportXML.SlaExceededBreakdown(e.getKey(), e.getValue()))
            .toList();

        ReportXML report = new ReportXML(
            new ReportXML.MachinesInfo(machines.size()),
            new ReportXML.WorkOrdersInfo(workOrders.size(), unfinishedXml),
            breakdownXml,
            slaExceededBreakdownsXml
        );

        Path targetPath = Path.of(filePath);
        XMLReport.saveToFile(targetPath, report);
        System.out.println("\nZapisano raport XML do pliku: " + targetPath.toAbsolutePath());

        System.out.println("\n## Plany z usługi producenta:");
        List<Machine.MachineType> plantTypes = machines.stream()
            .map(Machine::type)
            .distinct()
            .toList();

        for (Machine.MachineType t : plantTypes) {
            String status;
            try {
                status = vendorClient.plan(t)
                    .map(plan -> plan.preventiveIntervalDays() + " dni")
                    .orElse("brak typu w katalogu");
            } catch (RestClientException e) {
                status = "usługa niedostępna";
            }
            System.out.println("  " + t + ": " + status);
        }
    }
}
