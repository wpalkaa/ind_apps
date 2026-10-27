package pl.edu.ug.cmms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class XMLReport {
    private static final XmlMapper XML = XmlMapper.builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .build();

    private XMLReport() {
    }

    public static String toXml(ReportXML report) throws JsonProcessingException {
        return XML.writeValueAsString(report);
    }

    public static void saveToFile(Path path, ReportXML report) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, toXml(report));
    }

    public static ReportXML loadFromFile(Path path) throws IOException {
        return XML.readValue(Files.readString(path), ReportXML.class);
    }
}