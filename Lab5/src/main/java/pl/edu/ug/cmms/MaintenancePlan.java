package pl.edu.ug.cmms;

public record MaintenancePlan(
    String MachineType,
    int inspectionIntervalDays,
    int preventiveIntervalDays
) {
}
