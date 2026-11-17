package pl.edu.ug.cmms;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MaintenancePlan(
    @JsonProperty("machineType") String machineType,
    @JsonProperty("inspectionIntervalDays") int inspectionIntervalDays,
    @JsonProperty("preventiveIntervalDays") int preventiveIntervalDays
) {
}
