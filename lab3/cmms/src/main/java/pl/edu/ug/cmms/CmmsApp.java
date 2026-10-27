package pl.edu.ug.cmms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class CmmsApp {
    public static void main(String[] args) {
            // Tworzy kontener z beanami i uruchamia każdy ApplicationRunner
            SpringApplication.run(CmmsApp.class, args);
        }
}
