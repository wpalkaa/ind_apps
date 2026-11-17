package pl.edu.ug.cmms.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pl.edu.ug.cmms.config.SampleData;
import pl.edu.ug.cmms.domain.Machine;

import static org.junit.jupiter.api.Assertions.*;

class ReadMachinesDataTest {

    @Test
    @DisplayName("Import pliku CSV z błędnymi wierszami: odrzuca niepoprawne typy, daty, duplikaty w zakładzie i w pliku")
    void shouldRejectInvalidRowsAndAcceptValidOnes(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("test_machines.csv");
        String content = """
                id;name;type;hall;installedOn
                M-50;Tokarka CNC;CNC_LATHE;A;2024-01-10
                M-51;Laser przemysłowy;UNKNOWN_ROBOT;B;2024-02-15
                M-52;Prasa hydrauliczna;HYDRAULIC_PRESS;A;2024-02-31
                M-01;Tokarka istniejąca w zakładzie;CNC_LATHE;A;2024-03-01
                M-50;Powtórzony identyfikator w pliku;CNC_LATHE;C;2024-04-01
                
                M-53;Zbyt mało kolumn;CNC_LATHE
                M-54;Sprężarka nowa;COMPRESSOR;B;2025-05-05
                """;
        Files.writeString(csvFile, content);

        List<Machine> plantMachines = List.of(
                new Machine("M-01", "Tokarka CNC 1", Machine.MachineType.CNC_LATHE, "A", LocalDate.parse("2019-03-02"))
        );

        ReadMachinesData reader = new ReadMachinesData(csvFile);
        ImportResult result = reader.readMachines(plantMachines);

        assertEquals(2, result.acceptedCount());
        assertEquals(6, result.rejectedCount());

        List<String> acceptedIds = result.acceptedMachines().stream().map(Machine::id).toList();
        assertTrue(acceptedIds.contains("M-50"));
        assertTrue(acceptedIds.contains("M-54"));

        List<RejectedRow> rejected = result.rejectedRows();

        // Linia 3: UNKNOWN_ROBOT (nieznany typ)
        RejectedRow row3 = rejected.stream().filter(r -> r.lineNumber() == 3).findFirst().orElseThrow();
        assertTrue(row3.reason().contains("Nieznany typ maszyny"));
        assertTrue(row3.rawLine().contains("M-51"));

        // Linia 4: 2024-02-31 (błędna data)
        RejectedRow row4 = rejected.stream().filter(r -> r.lineNumber() == 4).findFirst().orElseThrow();
        assertTrue(row4.reason().contains("Niepoprawny format daty"));
        assertTrue(row4.rawLine().contains("M-52"));

        // Linia 5: M-01 (już istnieje w zakładzie)
        RejectedRow row5 = rejected.stream().filter(r -> r.lineNumber() == 5).findFirst().orElseThrow();
        assertTrue(row5.reason().contains("już istnieje w zakładzie"));
        assertTrue(row5.rawLine().contains("M-01"));

        // Linia 6: M-50 (duplikat w pliku)
        RejectedRow row6 = rejected.stream().filter(r -> r.lineNumber() == 6).findFirst().orElseThrow();
        assertTrue(row6.reason().contains("pojawił się już wcześniej w pliku"));
        assertTrue(row6.rawLine().contains("M-50"));

        // Linia 7: pusta linia
        RejectedRow row7 = rejected.stream().filter(r -> r.lineNumber() == 7).findFirst().orElseThrow();
        assertTrue(row7.reason().contains("Pusta linia"));

        // Linia 8: za mało kolumn
        RejectedRow row8 = rejected.stream().filter(r -> r.lineNumber() == 8).findFirst().orElseThrow();
        assertTrue(row8.reason().contains("wymagane 5 kolumn"));
    }

    @Test
    @DisplayName("Import domyślnego pliku data/new_machines.csv z maszynami zakładu SampleData")
    void shouldImportDefaultCsvFileCorrectly() throws IOException {
        Path defaultFile = Path.of("data/new_machines.csv");
        ReadMachinesData reader = new ReadMachinesData(defaultFile);
        List<Machine> plantMachines = SampleData.machines();

        ImportResult result = reader.readMachines(plantMachines);

        assertEquals(5, result.acceptedCount());
        assertEquals(4, result.rejectedCount());

        List<String> acceptedIds = result.acceptedMachines().stream().map(Machine::id).toList();
        assertEquals(List.of("M-25", "M-26", "M-28", "M-31", "M-32"), acceptedIds);

        List<Integer> rejectedLines = result.rejectedRows().stream().map(RejectedRow::lineNumber).toList();
        assertEquals(List.of(4, 6, 7, 9), rejectedLines);
    }
}
