package pl.edu.ug.cmms;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class WorkOrders {

    @JsonRootName("workOrders")
    public record WorkOrderList(
            @JacksonXmlElementWrapper(useWrapping = false)
            List<WorkOrder> workOrder
    ) {}

    private static final XmlMapper XML = XmlMapper.builder()
            .findAndAddModules()
            .build();

    private WorkOrders() {}

    public static List<WorkOrder> fromXml(String filePath) throws IOException {
        return XML.readValue(Files.readString(Path.of(filePath)), WorkOrderList.class).workOrder();
    }
}