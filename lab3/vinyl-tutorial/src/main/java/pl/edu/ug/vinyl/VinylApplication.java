package pl.edu.ug.vinyl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
// Dołącza definicje beanów z pliku XML; classpath: oznacza zasoby programu, czyli src/main/resources
@ImportResource("classpath:beans.xml")
public class VinylApplication {

    public static void main(String[] args) {
        // Tworzy kontener z beanami i uruchamia każdy ApplicationRunner
        SpringApplication.run(VinylApplication.class, args);
    }
}
