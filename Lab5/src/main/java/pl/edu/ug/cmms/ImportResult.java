package pl.edu.ug.cmms;

import java.util.List;

public record ImportResult(
    List<Machine> acceptedMachines,
    List<RejectedRow> rejectedRows
) {
    public int acceptedCount() {
        return acceptedMachines().size();
    }

    public int rejectedCount() {
        return rejectedRows().size();
    }
}
