package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class XmlReportWriter {

    private static final XmlMapper XML = XmlMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private XmlReportWriter() {
    }

    public static String toXml(XmlReportModel report) throws JsonProcessingException {
        return XML.writeValueAsString(report);
    }

    public static void saveToFile(Path path, XmlReportModel report) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, toXml(report));
    }
}
