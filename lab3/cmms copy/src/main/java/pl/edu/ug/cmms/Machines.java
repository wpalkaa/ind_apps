package pl.edu.ug.cmms;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Machines {

    @JsonRootName("machines")
    public record MachineList(
            @JacksonXmlElementWrapper(useWrapping = false)
            List<Machine> machine
    ) {}

    private static final XmlMapper XML = XmlMapper.builder()
            .findAndAddModules()
            .build();

    private Machines() {}

    public static List<Machine> fromXml(String filePath) throws IOException {
        return XML.readValue(Files.readString(Path.of(filePath)), MachineList.class).machine();
    }
}