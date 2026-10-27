package pl.edu.ug.cmms;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;

@JsonRootName("report")
public record ReportXML(
        MachinesInfo machines,
        WorkOrdersInfo workOrders,
        @JacksonXmlElementWrapper(localName = "topBreakdownMachines")
        List<MachineBreakdownXml> topBreakdownMachines
) {
    public record MachinesInfo(
            @JacksonXmlProperty(isAttribute = true)
            int count
    ) {}

    public record WorkOrdersInfo(
            @JacksonXmlProperty(isAttribute = true)
            int count,

            @JacksonXmlElementWrapper(useWrapping = false)
            @JacksonXmlProperty(localName = "unfinished")
            List<PriorityCountXml> unfinishedByPriority
    ) {}

    public record PriorityCountXml(
            @JacksonXmlProperty(isAttribute = true)
            WorkOrder.Priority priority,

            @JacksonXmlProperty(isAttribute = true)
            long count
    ) {}

    public record MachineBreakdownXml(
            @JacksonXmlProperty(isAttribute = true)
            String id,

            @JacksonXmlProperty(isAttribute = true)
            long breakdowns
    ) {}
}
