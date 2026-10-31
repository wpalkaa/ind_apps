package pl.edu.ug.cmms;

import pl.edu.ug.cmms.WorkOrder.Priority;
import pl.edu.ug.cmms.WorkOrder.WorkOrderStatus;
import pl.edu.ug.cmms.WorkOrder.WorkOrderType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component 
public class Tasks {

    private Tasks() {}
    
    public Map<Priority, Long> countUnfinishedOrders(List<WorkOrder> orders) {
        return orders.stream()
            .filter(order -> order.status() == WorkOrderStatus.IN_PROGRESS || order.status() == WorkOrderStatus.OPEN)
            .collect(Collectors.groupingBy(WorkOrder::priority, Collectors.counting()));
    }

    public List<Machine> topBreakdownMachines(
        List<WorkOrder> orders,
        List<Machine> machines,
        int limit
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
            .limit(limit)
            .map(entry -> machines.stream()
                    .filter(machine -> machine.id().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow()
            )
            .toList();
    }

    public Map<Priority, Long> countSlaExceededBreakdowns(List<WorkOrder> orders, Map<Priority, Duration> slaLimits) {
        return orders.stream()
            .filter(order -> order.type() == WorkOrderType.BREAKDOWN
                && order.status() == WorkOrderStatus.DONE)
            .filter(order -> {
                Duration limit = slaLimits.get(order.priority());
                Duration repairTime = Duration.between(order.reportedAt(), order.closedAt());
                return repairTime.compareTo(limit) > 0;
            })
            .collect(Collectors.groupingBy(WorkOrder::priority, Collectors.counting()));
    }

    public List<WorkOrder> unfinishedOrdersExceedingSla(
        List<WorkOrder> orders,
        Map<Priority, Duration> slaLimits,
        LocalDateTime now
    ) {
        return orders.stream()
            .filter(order -> order.status() == WorkOrderStatus.OPEN || order.status() == WorkOrderStatus.IN_PROGRESS)
            .filter(order -> {
                Duration limit = slaLimits.get(order.priority());
                Duration elapsed = Duration.between(order.reportedAt(), now);
                return elapsed.compareTo(limit) > 0;
            })
            .toList();
    }
}
