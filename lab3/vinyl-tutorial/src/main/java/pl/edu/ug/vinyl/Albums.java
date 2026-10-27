package pl.edu.ug.vinyl;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

public final class Albums {

    // Opis całego dokumentu: Jackson potrzebuje typu dla elementu głównego <albums>.
    // Nazwa składowej album musi odpowiadać elementowi <album>; inaczej lista będzie pusta
    @JsonRootName("albums")
    // useWrapping = false: elementy album stoją bezpośrednio w albums, bez elementu wokół listy
    public record AlbumList(@JacksonXmlElementWrapper(useWrapping = false) List<Album> album) {
    }

    // Jeden obiekt zamienia XML na obiekty Javy i odwrotnie
    private static final XmlMapper XML = new XmlMapper();

    private Albums() {
    }

    // Metoda fabryczna z argumentem: beans.xml przekazuje nazwę pliku jako constructor-arg
    // throws IOException: odczyt pliku może się nie udać
    public static List<Album> fromXml(String file) throws IOException {
        // Czyta cały plik jako napis, zamienia go na AlbumList i wyciąga z niego listę płyt
        return XML.readValue(Files.readString(Path.of(file)), AlbumList.class).album();
    }
}
