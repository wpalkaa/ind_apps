package pl.edu.ug.cmms.importer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.edu.ug.cmms.domain.Machine;

@Component 
public class ReadMachinesData {

    private final Path file;

    public ReadMachinesData(@Value("${cmms.import.machines-file}") Path file) {
        this.file = file;
    }

    public ImportResult readMachines(List<Machine> existingMachines) throws IOException {
        return readMachines(this.file, existingMachines);
    }

    public ImportResult readMachines(Path filePath, List<Machine> existingMachines) throws IOException {
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        Set<String> machinesId = new HashSet<>();
        
        if (existingMachines != null) {
            for (Machine m : existingMachines) {
                machinesId.add(m.id());
            }
        }

        Set<String> seenFileIds = new HashSet<>();
        List<Machine> acceptedRows = new ArrayList<>();
        List<RejectedRow> rejectedRows = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                rejectedRows.add(new RejectedRow(lineNumber, "Pusta linia", line));
                continue;
            }

            String[] fields = line.split(";");
            if (fields.length < 5) {
                rejectedRows.add(new RejectedRow(lineNumber, "Niepoprawny format wiersza (wymagane 5 kolumn)", line));
                continue;
            }

            String id = fields[0].trim();
            String name = fields[1].trim();
            String typeStr = fields[2].trim();
            String hall = fields[3].trim();
            String installedOnStr = fields[4].trim();

            if (machinesId.contains(id)) {
                rejectedRows.add(new RejectedRow(lineNumber, "Identyfikator już istnieje w zakładzie (" + id + ")", line));
                continue;
            }
            if (seenFileIds.contains(id)) {
                rejectedRows.add(new RejectedRow(lineNumber, "Identyfikator pojawił się już wcześniej w pliku (" + id + ")", line));
                continue;
            }

            Machine.MachineType type;
            try {
                type = Machine.MachineType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                rejectedRows.add(new RejectedRow(lineNumber, "Nieznany typ maszyny: " + typeStr, line));
                continue;
            }

            LocalDate installedOn;
            try {
                installedOn = LocalDate.parse(installedOnStr);
            } catch (DateTimeParseException e) {
                rejectedRows.add(new RejectedRow(lineNumber, "Niepoprawny format daty: " + installedOnStr, line));
                continue;
            }

            seenFileIds.add(id);
            acceptedRows.add(new Machine(id, name, type, hall, installedOn));
        }   

        return new ImportResult(acceptedRows, rejectedRows);
    }
}
