package pl.edu.ug.cmms;

import java.time.LocalDateTime;
import java.util.Objects;

public record WorkOrder(
    String id,
    String machineId,
    WorkOrderType type,
    Priority priority,
    WorkOrderStatus status,
    LocalDateTime reportedAt,
    LocalDateTime closedAt
) {
    public enum WorkOrderType {
        PREVENTIVE,
        INSPECTION,
        BREAKDOWN,
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        CRITICAL,
    }

    public enum WorkOrderStatus {
        OPEN,
        IN_PROGRESS,
        DONE,
        CANCELLED,
    }

    public WorkOrder {
        if( id == null || id.isEmpty() ) {
            throw new IllegalArgumentException("ID jest niepoprawne");
        }
        if( status == WorkOrderStatus.DONE && closedAt == null ) {
            throw new IllegalArgumentException("Zlecenie DONE musi mieć datę zamknięcia");
        }
        if( closedAt != null && closedAt.isBefore(reportedAt)) {
            throw new IllegalArgumentException("Data zamkniecia nie moze byc wcześniejsza niż zgłosznia");
        }
        Objects.requireNonNull(type, "Typ zlecenia nie może być null.");
        Objects.requireNonNull(priority, "Priorytet nie może być null.");
        Objects.requireNonNull(status, "Status nie może być null.");
        Objects.requireNonNull(reportedAt, "Data zgłoszenia nie może być null.");
    }
}