package pl.edu.ug.cmms.importer;

import java.util.List;
import pl.edu.ug.cmms.domain.Machine;

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
