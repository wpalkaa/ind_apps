package pl.edu.ug.cmms;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Machine> machines = SampleData.machines();
        List<WorkOrder> orders = SampleData.workOrders();

        System.out.println("## Niezakończone zlecenia wg priorytetu:");
        Map<Priority, Long> countUnfinishedOrders = countUnfinishedOrders(orders);
        countUnfinishedOrders.forEach((p, c) -> System.err.println("  " + p + ": " + c));

        System.out.println();

        System.out.println("## 3 maszyny z największą liczbą awarii:");
        List<Machine> top3 = top3BreakdowMachines(orders, machines);
        
       for (int i = 0; i < top3.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + top3.get(i));
        }
        
    }

    public static Map<Priority, Long> countUnfinishedOrders(List<WorkOrder> orders) {
        return orders.stream()
                .filter(order -> order.status() == WorkOrderStatus.OPEN || order.status() == WorkOrderStatus.IN_PROGRESS)
        .collect(Collectors.groupingBy(WorkOrder::priority, Collectors.counting()));
    }

    public static List<Machine> top3BreakdowMachines(List<WorkOrder> orders, List<Machine> machines) {
        Map<String, Long> breakdownsTotalPerMachine = orders.stream()
            .filter(order -> order.type() == WorkOrderType.BREAKDOWN
                    && order.status() != WorkOrderStatus.CANCELLED)
            .collect(Collectors.groupingBy(
                    WorkOrder::machineId,
                    Collectors.counting()
            ));

        return breakdownsTotalPerMachine.entrySet().stream()
            .sorted(
                    Map.Entry.<String, Long>comparingByValue()
                            .reversed()
                            .thenComparing(Map.Entry.comparingByKey())
            )
            .limit(3)
            .map(entry -> machines.stream()
                    .filter(machine -> machine.id().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow()
            )
            .toList();
    }
}