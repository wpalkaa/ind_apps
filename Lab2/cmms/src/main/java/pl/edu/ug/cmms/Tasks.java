package pl.edu.ug.cmms;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tasks {
    
    public Map<Priority, Long> countUnfinishedOrders(List<WorkOrder> orders) {
        return orders.stream()
            .filter(order -> order.status() == WorkOrderStatus.IN_PROGRESS || order.status() == WorkOrderStatus.OPEN)
            .collect(Collectors.groupingBy(WorkOrder::priority, Collectors.counting()));
    }

    public List<Machine> top3BreakdownMachines(
        List<WorkOrder> orders,
        List<Machine> machines
    ) {
        Map<String, Long> breakdownsTotalPerMachine = orders.stream()
            .filter(order ->
                    order.type() == WorkOrderType.BREAKDOWN
                    && order.status() == WorkOrderStatus.DONE
            )
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
