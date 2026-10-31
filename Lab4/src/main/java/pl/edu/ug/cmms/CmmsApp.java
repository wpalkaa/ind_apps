package pl.edu.ug.cmms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CmmsApp {
    public static void main(String[] args) {
            // Tworzy kontener z beanami i uruchamia każdy ApplicationRunner
            SpringApplication.run(CmmsApp.class, args);
        }
}
