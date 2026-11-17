package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class XMLReportTest {

    @Test
    @DisplayName("Generowanie XML i zapis do pliku")
    void shouldGenerateAndSaveXmlReport(@TempDir Path tempDir) throws IOException {
        ReportXML report = new ReportXML(
                new ReportXML.MachinesInfo(9),
                new ReportXML.WorkOrdersInfo(45, List.of(new ReportXML.PriorityCountXml(WorkOrder.Priority.HIGH, 3))),
                List.of(new ReportXML.MachineBreakdownXml("M-04", 5)),
                List.of(new ReportXML.SlaExceededBreakdown(WorkOrder.Priority.CRITICAL, 2))
        );

        String xml = XMLReport.toXml(report);
        assertNotNull(xml);
        assertTrue(xml.contains("<report>"));
        assertTrue(xml.contains("<machines count=\"9\"/>"));

        Path outputFile = tempDir.resolve("subfolder/test-raport.xml");
        XMLReport.saveToFile(outputFile, report);

        assertTrue(Files.exists(outputFile));
        String fileContent = Files.readString(outputFile);
        assertEquals(xml, fileContent);
    }
}
