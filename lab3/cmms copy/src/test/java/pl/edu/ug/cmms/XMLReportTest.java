package pl.edu.ug.cmms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XMLReportTest {

    @Test
    void shouldSaveReportToXmlAndReadItBackCorrectly(@TempDir Path tempDir) throws IOException {

        ReportXML.MachinesInfo machinesInfo = new ReportXML.MachinesInfo(9);
        ReportXML.WorkOrdersInfo workOrdersInfo = new ReportXML.WorkOrdersInfo(
                45,
                List.of(
                        new ReportXML.PriorityCountXml(WorkOrder.Priority.HIGH, 3),
                        new ReportXML.PriorityCountXml(WorkOrder.Priority.NORMAL, 1),
                        new ReportXML.PriorityCountXml(WorkOrder.Priority.LOW, 1)
                )
        );
        List<ReportXML.MachineBreakdownXml> breakdowns = List.of(
                new ReportXML.MachineBreakdownXml("M-04", 4),
                new ReportXML.MachineBreakdownXml("M-06", 4),
                new ReportXML.MachineBreakdownXml("M-08", 3)
        );

        ReportXML originalReport = new ReportXML(machinesInfo, workOrdersInfo, breakdowns);
        Path reportFile = tempDir.resolve("test-report.xml");

        XMLReport.saveToFile(reportFile, originalReport);

        ReportXML deserializedReport = XMLReport.loadFromFile(reportFile);

        assertNotNull(deserializedReport);
        assertEquals(originalReport, deserializedReport);
    }
}