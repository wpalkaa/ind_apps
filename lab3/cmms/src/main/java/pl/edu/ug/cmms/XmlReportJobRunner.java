package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class XmlReportJobRunner implements ApplicationRunner {
    
    private final Tasks tasks;
    private final List<Machine> machines;
    private final List<WorkOrder> workOrders;
    private final int topBreakdownMachinesLimit;
    private final String filePath;

    public XmlReportJobRunner(
        Tasks tasks,
        List<Machine> machines,
        List<WorkOrder> workOrders,
        int topBreakdownMachinesLimit,
        String filePath
    ) {
        this.tasks = tasks;
        this.machines = machines;
        this.workOrders = workOrders;
        this.topBreakdownMachinesLimit = topBreakdownMachinesLimit;
        this.filePath = filePath;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        System.out.println("## Niezakończone zlecenia wg priorytetu:");
        Map<WorkOrder.Priority, Long> countUnfinishedOrders = tasks.countUnfinishedOrders(workOrders);
        countUnfinishedOrders.forEach((p, c) -> System.err.println("  " + p + ": " + c));

        System.out.println();

        System.out.println("## 3 maszyny z największą liczbą awarii:");
        List<Machine> topMachines = tasks.topBreakdownMachines(workOrders, machines, topBreakdownMachinesLimit);
       for (int i = 0; i < topMachines.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + topMachines.get(i));
        }


        List<XmlReportModel.PriorityCountXml> unfinishedXml = countUnfinishedOrders.entrySet().stream()
                .map(e -> new XmlReportModel.PriorityCountXml(e.getKey(), e.getValue()))
                .toList();

        List<XmlReportModel.MachineBreakdownXml> breakdownXml = topMachines.stream()
                .map(machine -> {
                    long breakdowns = workOrders.stream()
                            .filter(wo -> wo.machineId().equals(machine.id())
                                    && wo.type() == WorkOrder.WorkOrderType.BREAKDOWN
                                    && wo.status() == WorkOrder.WorkOrderStatus.DONE)
                            .count();
                    return new XmlReportModel.MachineBreakdownXml(machine.id(), breakdowns);
                })
                .toList();

        XmlReportModel report = new XmlReportModel(
                new XmlReportModel.MachinesInfo(machines.size()),
                new XmlReportModel.WorkOrdersInfo(workOrders.size(), unfinishedXml),
                breakdownXml
        );

        Path targetPath = Path.of(filePath);
        XmlReportWriter.saveToFile(targetPath, report);
        System.out.println("\nZapisano raport XML do pliku: " + targetPath.toAbsolutePath());
    }
}
